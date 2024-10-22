package site.shasmatic.flutter_veepoo_sdk.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.VPWriteResponse
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import site.shasmatic.flutter_veepoo_sdk.statuses.PermissionStatuses

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
    private val discoveredDevices = mutableListOf<String>()
    private val sendEvent = SendEvent(bluetoothEventSink)
    private val currentGatt = vpManager.currentConnectGatt
    private val writeResponse: VPWriteResponse = VPWriteResponse()

    companion object {
        const val REQUEST_PERMISSIONS_CODE = 1001
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
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
                PermissionStatuses.GRANTED -> runBluetoothOperation(operation)
                PermissionStatuses.DENIED -> VPLogger.d("Bluetooth permissions denied.")
                PermissionStatuses.PERMANENTLY_DENIED -> VPLogger.d("Bluetooth permissions permanently denied.")
                PermissionStatuses.RESTRICTED -> VPLogger.d("Bluetooth permissions restricted by device policy.")
                PermissionStatuses.UNKNOWN -> VPLogger.d("Bluetooth permissions status unknown.")
            }
        }
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
            if (isEnabled) {
                startScanDevices()
            } else {
                VPLogger.w("Bluetooth is disabled or the scanner is not initialized")
            }
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
           vpManager.registerConnectStatusListener(address, bleConnectStatusListener)
           vpManager.connectDevice(address, connectResponseCallBack, notifyResponseCallBack)
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
            vpManager.disconnectWatch(writeResponse)
            vpManager.unregisterConnectStatusListener(currentGatt.device.address, bleConnectStatusListener)
        }
    }

    private fun startScanDevices() = vpManager.startScanDevice(searchResponseCallBack)

    private val searchResponseCallBack = object : SearchResponse {
        override fun onSearchStarted() = VPLogger.i("Bluetooth scan started")

        override fun onDeviceFounded(result: SearchResult?) {
            result?.let {
                if (!discoveredDevices.contains(it.address)) {
                    discoveredDevices.add(it.address)
                    sendEvent.sendBluetoothEvent(mapOf("name" to it.name, "address" to it.address, "rssi" to it.rssi))
                }
            }
        }

        override fun onSearchStopped() = VPLogger.i("Bluetooth scan stopped")

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
        IConnectResponse { state, _, success ->
            if (!isSubmitted) {
                isSubmitted = true
                if (success) {
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
            if (state == Constants.REQUEST_SUCCESS) {
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