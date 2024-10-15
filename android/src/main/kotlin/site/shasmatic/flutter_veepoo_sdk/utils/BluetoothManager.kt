package site.shasmatic.flutter_veepoo_sdk.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.model.BleGattProfile
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IABleConnectStatusListener
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.base.IConnectResponse
import com.veepoo.protocol.listener.base.INotifyResponse
import com.veepoo.protocol.listener.data.ICustomSettingDataListener
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener
import com.veepoo.protocol.listener.data.IPwdDataListener
import com.veepoo.protocol.listener.data.ISocialMsgDataListener
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData
import com.veepoo.protocol.model.datas.FunctionSocailMsgData
import com.veepoo.protocol.model.datas.PwdData
import com.veepoo.protocol.model.enums.EPwdStatus
import com.veepoo.protocol.model.settings.CustomSettingData
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import site.shasmatic.flutter_veepoo_sdk.VeepooLogger
import java.lang.reflect.InvocationTargetException

/**
 * Manages Bluetooth operations for the Flutter Veepoo SDK plugin.
 *
 * @constructor Initializes the [BluetoothManager] with the given [DeviceStorage], [MethodChannel.Result], [Activity], [EventChannel.EventSink], and [VPOperateManager].
 * @param deviceStorage An instance of [DeviceStorage] used for local storage interactions.
 * @param result The result instance for the current method call.
 * @param activity The activity instance to be associated with this manager.
 * @param bluetoothEventSink The EventSink instance to be used for Bluetooth scan events.
 * @param vpManager An instance of [VPOperateManager] used to control operations on the wearable device.
 * @author Ahmad Rifa'i
 */
class BluetoothManager(
    private val deviceStorage: DeviceStorage,
    private val result: MethodChannel.Result,
    private val activity: Activity,
    private val bluetoothEventSink: EventChannel.EventSink?,
    private val vpManager: VPOperateManager
) {

    private var isEnabled: Boolean = false
    private var isSubmitted: Boolean = false
    private var macAddress: String = ""
    private val discoveredDevices: MutableList<String> = mutableListOf<String>()
    private val sendEvent: SendEvent = SendEvent(bluetoothEventSink)

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 1001
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_ENABLE_LOCATION = 2
    }

    /**
     * Requests the necessary permissions for Bluetooth operations.
     *
     * This method checks if the required permissions are granted and requests them if not.
     * The permissions required are:
     * - ACCESS_FINE_LOCATION
     * - BLUETOOTH
     * - BLUETOOTH_ADMIN
     * - BLUETOOTH_SCAN
     * - BLUETOOTH_CONNECT
     * @see requiredPermissions
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestBluetoothPermissions() {
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(activity, requiredPermissions(), REQUEST_PERMISSIONS_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun arePermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requiredPermissions(): Array<String> {
        return arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    /**
     * Initializes the Bluetooth scanner.
     *
     * This method checks if the Bluetooth adapter is enabled and initializes the scanner if it is.
     * If the adapter is disabled, the method logs a warning message.
     */
    fun scanDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isEnabled) {
            startScanDevices()
        } else {
            VeepooLogger.w("Bluetooth is disabled or the scanner is not initialized")
        }
    }

    /**
     * Stops the Bluetooth scanner.
     *
     * This method stops the Bluetooth scanner and clears the list of discovered devices.
     */
    fun stopScanDevices() {
        vpManager.stopScanDevice()
        discoveredDevices.clear()
    }

    /**
     * Bind the device with the given password.
     *
     * This method binds the device with the given password and 24-hour format preference.
     *
     * @param password The password to bind the device with.
     * @param is24H A flag indicating whether the 24-hour format is preferred.
     */
    fun bindDevice(password: String, is24H: Boolean) {
        vpManager.confirmDevicePwd(
            writeResponseCallBack,
            passwordDataListener(password, is24H),
            deviceFuncDataListener,
            socialMessageDataListener,
            customSettingDataListener,
            password, is24H
        )
    }

    /**
     * Connect to a Bluetooth device with the given address.
     *
     * This method connects to the Bluetooth device with the given address and registers the connect status listener.
     *
     * @param address The MAC address of the device to connect to.
     */
    fun connectDevice(address: String) {
        macAddress = address
        vpManager.registerConnectStatusListener(macAddress, bleConnectStatusListener)
        vpManager.connectDevice(macAddress, connectResponseCallBack, notifyResponseCallBack)
    }

    /**
     * Disconnect from the connected Bluetooth device.
     *
     * This method disconnects from the connected Bluetooth device.
     */
    fun disconnectDevice() {
        try {
            vpManager.disconnectWatch(writeResponseCallBack)
        } catch (e: InvocationTargetException) {
            VeepooLogger.e("Failed to disconnect from device: ${e.message}", e.cause)
        } catch (e: Exception) {
            VeepooLogger.e("Failed to disconnect from device: ${e.message}", e.cause)
        }
    }

    private fun startScanDevices() {
        vpManager.startScanDevice(searchResponseCallBack)
    }

    private val searchResponseCallBack = object : SearchResponse {
        override fun onSearchStarted() {
            VeepooLogger.i("Bluetooth scan started")
        }

        override fun onDeviceFounded(result: SearchResult?) {
            VeepooLogger.i("Device found")
            result?.let {
                if (discoveredDevices.add(it.address)) {
                    val scanResult = mapOf<String, Any>(
                        "name" to it.name,
                        "address" to it.address,
                        "rssi" to it.rssi
                    )

                    sendEvent.sendBluetoothEvent(scanResult)
                }
            }
        }

        override fun onSearchStopped() {
            VeepooLogger.i("Bluetooth scan stopped")
        }

        override fun onSearchCanceled() {
            VeepooLogger.i("Bluetooth scan canceled")
        }
    }

    private val bleConnectStatusListener = object : IABleConnectStatusListener() {
        override fun onConnectStatusChanged(address: String?, status: Int) {
            if (status == Constants.STATUS_CONNECTED) {
                VeepooLogger.i("Connected to device")
            } else if (status == Constants.STATUS_DISCONNECTED) {
                VeepooLogger.i("Disconnected from device")
            }
        }
    }

    private val connectResponseCallBack = object : IConnectResponse {
        override fun connectState(state: Int, gatt: BleGattProfile?, success: Boolean) {
            if (!isSubmitted) {
                isSubmitted = true
                if (success) {
                    result.success("Connected to device")
                } else {
                    VeepooLogger.e("Failed to connect to device: $state")
                    result.error("CONNECT_FAILED", "Failed to connect to device", null)
                }
            }
        }
    }

    private val notifyResponseCallBack = object : INotifyResponse {
        override fun notifyState(state: Int) {
            if (!isSubmitted) {
                isSubmitted = true
                if (state == Constants.REQUEST_SUCCESS) {
                    result.success("Notification enabled")
                } else {
                    VeepooLogger.e("Failed to enable notification: $state")
                    result.error("NOTIFY_FAILED", "Failed to enable notification", null)
                }
            }
        }
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            VeepooLogger.i("Write response: $status")
        }
    }

    private fun passwordDataListener(password: String, is24H: Boolean): IPwdDataListener {
        return object : IPwdDataListener{
            override fun onPwdDataChange(data: PwdData?) {
                VeepooLogger.i("Password binding result: $data")
                when(data?.getmStatus()) {
                    EPwdStatus.CHECK_FAIL -> VeepooLogger.e("Password check failed")
                    EPwdStatus.CHECK_SUCCESS -> VeepooLogger.i("Password check success")
                    EPwdStatus.SETTING_FAIL -> VeepooLogger.e("Password setting failed")
                    EPwdStatus.SETTING_SUCCESS -> VeepooLogger.i("Password setting success")
                    EPwdStatus.READ_FAIL -> VeepooLogger.e("Password read failed")
                    EPwdStatus.READ_SUCCESS -> VeepooLogger.i("Password read success")
                    EPwdStatus.CHECK_AND_TIME_SUCCESS -> deviceStorage.saveCredentials(macAddress, password, is24H)
                    EPwdStatus.UNKNOW -> VeepooLogger.e("Unknown password status")
                    null -> VeepooLogger.e("Password status is null")
                }
            }
        }
    }

    private val deviceFuncDataListener = object : IDeviceFuctionDataListener {
        override fun onFunctionSupportDataChange(data: FunctionDeviceSupportData?) {
            VeepooLogger.i("Device function data: $data")
        }
    }

    private val socialMessageDataListener = object : ISocialMsgDataListener {
        override fun onSocialMsgSupportDataChange(data: FunctionSocailMsgData?) {
            VeepooLogger.i("Social message data change 1: $data")
        }

        override fun onSocialMsgSupportDataChange2(data: FunctionSocailMsgData?) {
            VeepooLogger.i("Social message data change 2: $data")
        }
    }

    private val customSettingDataListener = object : ICustomSettingDataListener {
        override fun OnSettingDataChange(data: CustomSettingData?) {
            VeepooLogger.i("Custom setting data: $data")
        }
    }
}