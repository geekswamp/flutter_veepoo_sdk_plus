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
import site.shasmatic.flutter_veepoo_sdk.DeviceBindingStatus
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import java.lang.reflect.InvocationTargetException

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

    private var isEnabled: Boolean = true
    private var isSubmitted: Boolean = false
    private val discoveredDevices: MutableList<String> = mutableListOf()
    private val sendEvent: SendEvent = SendEvent(bluetoothEventSink)

    companion object {
        private const val REQUEST_PERMISSIONS_CODE = 1001
    }

    /**
     * Requests the necessary permissions for Bluetooth operations.
     *
     * This method should be called before scanning for devices.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestBluetoothPermissions() {
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(activity, requiredPermissions(), REQUEST_PERMISSIONS_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun arePermissionsGranted(): Boolean {
        return requiredPermissions().all {
            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requiredPermissions(): Array<String> {
        return arrayOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * Scans all available Bluetooth devices.
     */
    fun scanDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isEnabled) {
            startScanDevices()
        } else {
            VPLogger.w("Bluetooth is disabled or the scanner is not initialized")
        }
    }

    /**
     * Stops scanning for Bluetooth devices.
     */
    fun stopScanDevices() {
        vpManager.stopScanDevice()
        discoveredDevices.clear()
    }

    /**
     * Binds the device with the given password and 24-hour mode status.
     *
     * @param password The password used to bind the device.
     * @param is24H A boolean indicating whether the device is in 24-hour mode.
     * @param onStatus A callback function to handle the binding status.
     */
    fun bindDevice(password: String, is24H: Boolean, onStatus: (DeviceBindingStatus?) -> Unit) {
        vpManager.confirmDevicePwd(
            writeResponseCallBack,
            passwordDataListener(password, is24H, onStatus),
            deviceFuncDataListener,
            socialMessageDataListener,
            customSettingDataListener,
            password, is24H
        )
    }

    /**
     * Connects to the device with the given MAC address.
     *
     * @param address The MAC address of the device to connect to.
     */
    fun connectDevice(address: String) {
        vpManager.registerConnectStatusListener(address, bleConnectStatusListener)
        vpManager.connectDevice(address, connectResponseCallBack, notifyResponseCallBack)
    }

    /**
     * Disconnects from the connected device.
     */
    fun disconnectDevice() {
        try {
            if (isDeviceConnected()) {
                vpManager.disconnectWatch(writeResponseCallBack)
                vpManager.unregisterConnectStatusListener(deviceStorage.getAddress(), bleConnectStatusListener)
                deviceStorage.saveAddress(null)
            } else {
                VPLogger.e("No device is currently connected")
            }
        } catch (e: InvocationTargetException) {
            VPException("Failed to disconnect from device: ${e.targetException.message}", e.targetException.cause)
        } catch (e: Exception) {
            VPException("Failed to disconnect from device: ${e.message}", e.cause)
        }
    }

    /**
     * Checks if the device is currently connected.
     *
     * @return A boolean indicating whether the device is connected.
     */
    fun isDeviceConnected(): Boolean {
        return vpManager.isDeviceConnected(deviceStorage.getAddress())
    }

    private fun startScanDevices() {
        vpManager.startScanDevice(searchResponseCallBack)
    }

    private val searchResponseCallBack = object : SearchResponse {
        override fun onSearchStarted() {
            VPLogger.i("Bluetooth scan started")
        }

        override fun onDeviceFounded(result: SearchResult?) {
            VPLogger.i("Device found")
            result?.let {
                if (discoveredDevices.add(it.address)) {
                    val scanResult = mapOf(
                        "name" to it.name,
                        "address" to it.address,
                        "rssi" to it.rssi
                    )
                    sendEvent.sendBluetoothEvent(scanResult)
                }
            }
        }

        override fun onSearchStopped() {
            VPLogger.i("Bluetooth scan stopped")
        }

        override fun onSearchCanceled() {
            VPLogger.i("Bluetooth scan canceled")
        }
    }

    private val bleConnectStatusListener = object : IABleConnectStatusListener() {
        override fun onConnectStatusChanged(address: String?, status: Int) {
            when (status) {
                Constants.STATUS_CONNECTED -> {
                    val currentAddress = deviceStorage.saveAddress(address)
                    VPLogger.i("Connected to device: $currentAddress")
                }
                Constants.STATUS_DISCONNECTED -> {
                    deviceStorage.saveAddress(null)
                    VPLogger.i("Disconnected from device, resetting currentAddress.")
                }
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
                    VPLogger.e("Failed to connect to device: $state")
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
                    VPLogger.e("Failed to enable notification: $state")
                    result.error("NOTIFY_FAILED", "Failed to enable notification", null)
                }
            }
        }
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            VPLogger.i("Write response: $status")
        }
    }

    private fun passwordDataListener(password: String, is24H: Boolean, onStatus: (DeviceBindingStatus?) -> Unit): IPwdDataListener {
        return object : IPwdDataListener {
            override fun onPwdDataChange(data: PwdData?) {
                VPLogger.i("Password binding result: $data")

                val status = when (data?.getmStatus()) {
                    EPwdStatus.CHECK_FAIL -> DeviceBindingStatus.CHECK_FAIL
                    EPwdStatus.UNKNOW -> DeviceBindingStatus.UNKNOWN
                    EPwdStatus.CHECK_SUCCESS -> DeviceBindingStatus.CHECK_SUCCESS
                    EPwdStatus.SETTING_FAIL -> DeviceBindingStatus.SETTING_FAIL
                    EPwdStatus.SETTING_SUCCESS -> DeviceBindingStatus.SETTING_SUCCESS
                    EPwdStatus.READ_FAIL -> DeviceBindingStatus.READ_FAIL
                    EPwdStatus.READ_SUCCESS -> DeviceBindingStatus.READ_SUCCESS
                    EPwdStatus.CHECK_AND_TIME_SUCCESS -> {
                        deviceStorage.saveCredentials(password, is24H)
                        DeviceBindingStatus.CHECK_AND_TIME_SUCCESS
                    }
                    null -> null
                }

                onStatus(status)
            }
        }
    }

    private val deviceFuncDataListener = object : IDeviceFuctionDataListener {
        override fun onFunctionSupportDataChange(data: FunctionDeviceSupportData?) {
            VPLogger.i("Device function data: $data")
        }
    }

    private val socialMessageDataListener = object : ISocialMsgDataListener {
        override fun onSocialMsgSupportDataChange(data: FunctionSocailMsgData?) {
            VPLogger.i("Social message data change 1: $data")
        }

        override fun onSocialMsgSupportDataChange2(data: FunctionSocailMsgData?) {
            VPLogger.i("Social message data change 2: $data")
        }
    }

    private val customSettingDataListener = object : ICustomSettingDataListener {
        override fun OnSettingDataChange(data: CustomSettingData?) {
            VPLogger.i("Custom setting data: $data")
        }
    }
}