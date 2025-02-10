package site.shasmatic.flutter_veepoo_sdk.utils

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.inuker.bluetooth.library.Code
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IABleConnectStatusListener
import com.veepoo.protocol.listener.base.IConnectResponse
import com.veepoo.protocol.listener.base.INotifyResponse
import com.veepoo.protocol.listener.data.ICustomSettingDataListener
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener
import com.veepoo.protocol.listener.data.IPwdDataListener
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.enums.EPwdStatus
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.VPWriteResponse
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import site.shasmatic.flutter_veepoo_sdk.statuses.PermissionStatuses
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Manages Bluetooth operations for the Flutter Veepoo SDK plugin.
 *
 * @constructor Initializes the [VPBluetoothManager] with the given [DeviceStorage], [MethodChannel.Result], [Activity], [EventChannel.EventSink], and [VPOperateManager].
 * @param deviceStorage An instance of [DeviceStorage] used for local storage interactions.
 * @param result The result instance for the current method call.
 * @param activity The activity instance to be associated with this manager.
 * @param bluetoothEventSink The EventSink instance to be used for Bluetooth scan events.
 * @param vpManager An instance of [VPOperateManager] used to control operations on the wearable device.
 */
class VPBluetoothManager(
    private val deviceStorage: DeviceStorage,
    private val result: MethodChannel.Result,
    private val activity: Activity,
    private val bluetoothEventSink: EventChannel.EventSink?,
    private val vpManager: VPOperateManager
) {

    private var isEnabled = true
    private var isSubmitted = false
    private var permissionCallback: ((PermissionStatuses) -> Unit)? = null
    private val discoveredDevices = mutableMapOf<String, Map<String, Any>>()
    private val sendEvent = SendEvent(bluetoothEventSink)
    private val currentGatt = vpManager.currentConnectGatt
    private val writeResponse: VPWriteResponse = VPWriteResponse()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var retryCount: Int = 0
    private var scanJob: Job? = null
    private var operationTimeout: Job? = null
    private var isPowerSaveMode = false
    private var lastScanTime: Long = 0

    companion object {
        const val REQUEST_PERMISSIONS_CODE = 1001
        const val MAX_RETRY_ATTEMPTS = 3
        const val OPERATION_TIMEOUT_MS = 30000L
        const val SCAN_TIMEOUT_MS = 60000L
//        const val RSSI_THRESHOLD = 10
    }

    /**
     * Initializes Bluetooth adapter based on Android version
     */
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (activity.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    /**
     * Requests Bluetooth permissions from the user.
     */
    fun requestBluetoothPermissions() {
        if (arePermissionsGranted()) {
            result.success(PermissionStatuses.GRANTED.name)
        } else {
            requestPermissions { status -> result.success(status.name)}
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val requiredPermissions = getRequiredPermissions()
        return requiredPermissions.all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            else -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }

    private suspend fun retryWithExponentialBackoff(
        maxAttempts: Int = MAX_RETRY_ATTEMPTS,
        initialDelayMs: Long = 1000,
        maxDelayMs: Long = 10000,
        operation: suspend () -> Unit
    ) {
        var currentDelay = initialDelayMs
        repeat(maxAttempts) { attempt ->
            try {
                operation()
                return
            } catch (e: Exception) {
                if (attempt == maxAttempts - 1) throw e
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(maxDelayMs)
            }
        }
    }

    private fun setupOperationTimeout(timeoutMs: Long = OPERATION_TIMEOUT_MS) {
        operationTimeout?.cancel()
        operationTimeout = coroutineScope.launch {
            delay(timeoutMs)
            if (!isSubmitted) {
                isSubmitted = true
                result.error(
                    "OPERATION_TIMEOUT",
                    "Operation timed out after ${timeoutMs}ms",
                    null
                )
            }
        }
    }

    private fun requestPermissions(callback: (PermissionStatuses) -> Unit) {
        ActivityCompat.requestPermissions(
            activity,
            getRequiredPermissions(),
            REQUEST_PERMISSIONS_CODE
        )
        permissionCallback = callback
    }

    /**
     * Handles the result of a permission request.
     *
     * @param requestCode The request code for the permission request.
     * @param grantResults The results of the permission request.
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            val status = when {
                grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> PermissionStatuses.GRANTED
                arePermissionsPermanentlyDenied(getRequiredPermissions()) -> PermissionStatuses.PERMANENTLY_DENIED
                grantResults.any { it == PackageManager.PERMISSION_DENIED } -> PermissionStatuses.DENIED
                else -> PermissionStatuses.UNKNOWN
            }
            result.success(status.name)
            permissionCallback?.invoke(status)
            permissionCallback = null
        }
    }

    private fun arePermissionsPermanentlyDenied(permissions: Array<String>): Boolean {
        return permissions.any {
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }

    private fun executeBluetoothActionWithPermission(operation: () -> Unit) {
        checkAndRequestBluetoothPermissions { status ->
            when (status) {
                PermissionStatuses.GRANTED -> {
                    try {
                        if (!bluetoothAdapter?.isEnabled!!) {
                            result.error("BLUETOOTH_DISABLED", "Bluetooth is not enabled", null)
                            return@checkAndRequestBluetoothPermissions
                        }
                        runBluetoothOperation(operation)
                    } catch (e: Exception) {
                        handleBluetoothError(e)
                    }
                }
                else -> handlePermissionDenied(status)
            }
        }
    }

    private fun handleBluetoothError(error: Exception) {
        val errorMessage = when (error) {
            is SecurityException -> "Bluetooth permission issue: ${error.message}"
            is IllegalStateException -> "Bluetooth adapter issue: ${error.message}"
            else -> "Bluetooth operation error: ${error.message}"
        }
        VPLogger.e(errorMessage)
        result.error("BLUETOOTH_ERROR", errorMessage, error.cause)
    }

    private fun handlePermissionDenied(status: PermissionStatuses) {
        val message = when (status) {
            PermissionStatuses.DENIED -> "Bluetooth permissions denied"
            PermissionStatuses.PERMANENTLY_DENIED -> "Bluetooth permissions permanently denied"
            PermissionStatuses.RESTRICTED -> "Bluetooth permissions restricted by device policy"
            else -> "Unknown permission status"
        }
        VPLogger.w(message)
        result.error("PERMISSION_ERROR", message, null)
    }

    private fun checkAndRequestBluetoothPermissions(callback: (PermissionStatuses) -> Unit) {
        if (arePermissionsGranted()) {
            callback(PermissionStatuses.GRANTED)
        } else {
            requestPermissions(callback)
        }
    }

    private fun runBluetoothOperation(operation: () -> Unit) {
        try {
            operation()
        } catch (e: SecurityException) {
            throw VPException("Bluetooth permission issue: ${e.message}", e.cause)
        } catch (e: Exception) {
            throw VPException("Error during Bluetooth operation: ${e.message}", e.cause)
        }
    }

    /**
     * Opens the app settings page to allow the user to manually enable permissions.
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    /**
     * Checks if Bluetooth is enabled.
     *
     * @return `true` if Bluetooth is enabled, `false` otherwise.
     */
    fun isBluetoothEnabled(): Boolean {
        val isEnabled = vpManager.isBluetoothOpened
        result.success(isEnabled)
        return isEnabled
    }

    /**
     * Opens Bluetooth.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun openBluetooth() = executeBluetoothActionWithPermission { vpManager.openBluetooth() }

    /**
     * Closes Bluetooth.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun closeBluetooth() = executeBluetoothActionWithPermission { vpManager.closeBluetooth() }

    /**
     * Scans for nearby Bluetooth devices.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun scanDevices() {
        executeBluetoothActionWithPermission {
            if (!isEnabled || isPowerSaveMode) {
                VPLogger.w("Bluetooth is disabled or device is in power save mode")
                return@executeBluetoothActionWithPermission
            }

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastScanTime < TimeUnit.MINUTES.toMillis(1)) {
                VPLogger.w("Scanning too frequently, please wait")
                return@executeBluetoothActionWithPermission
            }

            scanJob?.cancel()
            scanJob = coroutineScope.launch {
                try {
                    startScanDevices()
                    delay(SCAN_TIMEOUT_MS)
                    stopScanDevices()
                } catch (e: Exception) {
                    VPLogger.e("Scan error: ${e.message}")
                    stopScanDevices()
                }
            }
            lastScanTime = currentTime
        }
    }

    /**
     * Stops scanning for nearby Bluetooth devices.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun stopScanDevices() {
        executeBluetoothActionWithPermission {
            vpManager.stopScanDevice()
            discoveredDevices.clear()
        }
    }

    /**
     * Binds the device with the given password and 24-hour time setting.
     *
     * @param password The password to bind the device with.
     * @param is24H `true` if the device should use 24-hour time format, `false` otherwise.
     */
    fun bindDevice(password: String, is24H: Boolean) {
        vpManager.confirmDevicePwd(
            writeResponse,
            passwordDataListener(password, is24H),
            deviceFuncDataListener,
            socialMessageDataListener,
            customSettingDataListener,
            password, is24H
        )
    }

    /**
     * Connects to the device with the given address.
     *
     * @param address The address of the device to connect to.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun connectDevice(address: String) {
        executeBluetoothActionWithPermission {
            coroutineScope.launch {
                retryWithExponentialBackoff {
                    vpManager.registerConnectStatusListener(address, bleConnectStatusListener)
                    setupOperationTimeout()
                    vpManager.connectDevice(address, connectResponseCallBack, notifyResponseCallBack)
                }
            }
        }
    }

    /**
     * Gets the address of the currently connected device.
     */
    fun getAddress() = result.success(currentGatt.device.address)

    /**
     * Gets current status of the connected device.
     *
     * @return The current status of the connected device.
     */
    fun getCurrentStatus(): Int {
        val status = vpManager.getConnectStatus(currentGatt.device.address)
        result.success(status)
        return status
    }

    /**
     * Checks if the device is connected.
     *
     * @return `true` if the device is connected, `false` otherwise.
     */
    fun isDeviceConnected(): Boolean {
        val isConnected = vpManager.isCurrentDeviceConnected
        result.success(isConnected)
        return isConnected
    }

    /**
     * Starts detecting heart rate.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun disconnectDevice() {
        executeBluetoothActionWithPermission {
            coroutineScope.launch {
                try {
                    vpManager.disconnectWatch(writeResponse)
                    vpManager.unregisterConnectStatusListener(currentGatt.device.address, bleConnectStatusListener)
                    cleanupResources()
                } catch (e: Exception) {
                    VPLogger.e("Disconnect error: ${e.message}")
                    throw VPException("Error during disconnect: ${e.message}", e.cause)
                }
            }
        }
    }

    private fun cleanupResources() {
        scanJob?.cancel()
        operationTimeout?.cancel()
        coroutineScope.cancel()
        discoveredDevices.clear()
        retryCount = 0
        isSubmitted = false
    }

    private fun startScanDevices() = vpManager.startScanDevice(searchResponseCallBack)

    private val searchResponseCallBack = object : SearchResponse {
        override fun onSearchStarted() {
            VPLogger.i("Bluetooth scan started")
            discoveredDevices.clear()
        }

        override fun onDeviceFounded(result: SearchResult?) {
            result?.let {
                val deviceMap = mapOf(
                    "name" to it.name,
                    "address" to it.address,
                    "rssi" to it.rssi
                )

                if (discoveredDevices.put(it.address, deviceMap) == null) {
                    sendEvent.sendBluetoothEvent(discoveredDevices.values.toList())
                } else {
                    val existingDevice = discoveredDevices[it.address]
                    if (existingDevice != null && abs(existingDevice["rssi"] as Int - it.rssi) > 10) {
                        discoveredDevices[it.address] = deviceMap
                        sendEvent.sendBluetoothEvent(discoveredDevices.values.toList())
                    }
                }
            }
        }

        override fun onSearchStopped() {
            VPLogger.i("Bluetooth scan stopped")
            sendEvent.sendBluetoothEvent(discoveredDevices.values.toList())
        }

        override fun onSearchCanceled() = VPLogger.i("Bluetooth scan canceled")
    }

    private val bleConnectStatusListener = object : IABleConnectStatusListener() {
        override fun onConnectStatusChanged(address: String?, status: Int) {
            when (status) {
                Constants.STATUS_CONNECTED -> VPLogger.i("Connected to device: $address")
                Constants.STATUS_DISCONNECTED -> VPLogger.i("Disconnected from device, resetting $address.")
            }
        }
    }

    private val connectResponseCallBack =
        IConnectResponse { state, _, _ ->
            if (!isSubmitted) {
                isSubmitted = true
                if (state == Code.REQUEST_SUCCESS) {
                    result.success("Connected to device")
                } else {
                    VPLogger.e("Failed to connect to device: $state")
                    result.error("CONNECT_FAILED", "Failed to connect to device", state)
                }
            }
        }

    private val notifyResponseCallBack = INotifyResponse { state ->
        if (!isSubmitted) {
            isSubmitted = true
            if (state == Code.REQUEST_SUCCESS) {
                result.success("Notification enabled")
            } else {
                VPLogger.e("Failed to enable notification: $state")
                result.error("NOTIFY_FAILED", "Failed to enable notification", state)
            }
        }
    }

    private fun passwordDataListener(password: String, is24H: Boolean) =
        IPwdDataListener { data ->
            if (!isSubmitted) {
                isSubmitted = true
                if (data.getmStatus() == EPwdStatus.CHECK_AND_TIME_SUCCESS) {
                    deviceStorage.saveCredentials(password, is24H)
                }
                result.success(data.getmStatus().name)
            } else {
                VPLogger.w("Reply already submitted for passwordDataListener")
            }
        }

    private val deviceFuncDataListener = IDeviceFuctionDataListener { data -> VPLogger.i("Device function data: $data") }

    private val socialMessageDataListener = object : ISocialMsgDataListener {
        override fun onSocialMsgSupportDataChange(data: FunctionSocailMsgData?) = VPLogger.i("Social message data change 1: $data")
        override fun onSocialMsgSupportDataChange2(data: FunctionSocailMsgData?) = VPLogger.i("Social message data change 2: $data")
    }

    private val customSettingDataListener = ICustomSettingDataListener { data -> VPLogger.i("Custom setting data: $data") }
}