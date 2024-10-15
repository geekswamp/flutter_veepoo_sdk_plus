package site.shasmatic.flutter_veepoo_sdk

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.shareprence.VpSpGetUtil
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import site.shasmatic.flutter_veepoo_sdk.utils.DeviceStorage
import site.shasmatic.flutter_veepoo_sdk.utils.HeartRate
import site.shasmatic.flutter_veepoo_sdk.utils.VPBluetoothManager

/**
 * Handles method calls from Flutter to perform various operations related to Bluetooth management,
 * heart rate detection, and blood oxygen (SpO2) detection.
 *
 * @constructor Initializes the [VPMethodChannelHandler] with the given [VPOperateManager], [VpSpGetUtil], and [DeviceStorage].
 * @param vpManager An instance of [VPOperateManager] used to control operations on the wearable device.
 * @param vpSpGetUtil An instance of [VpSpGetUtil] used to access shared preferences for device settings.
 * @param deviceStorage An instance of [DeviceStorage] used for local storage interactions.
 */
class VPMethodChannelHandler(
    private val vpManager: VPOperateManager,
    private val vpSpGetUtil: VpSpGetUtil,
    private val deviceStorage: DeviceStorage,
): MethodChannel.MethodCallHandler, PluginRegistry.RequestPermissionsResultListener {

    private var result: MethodChannel.Result? = null
    private var activity: Activity? = null
    private var scanBluetoothEventSink: EventChannel.EventSink? = null
    private var detectHeartEventSink: EventChannel.EventSink? = null
    private var detectSpohEventSink: EventChannel.EventSink? = null

    companion object {
        private const val REQUEST_PERMISSIONS = 2
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        val address = call.argument<String>("address")
        val password = call.argument<String>("password")
        val is24H = call.argument<Boolean>("is24H")
        this.result = result

        when(call.method) {
            "requestBluetoothPermissions" -> handleRequestBluetoothPermissions()
            "scanDevices" -> handleScanDevices()
            "stopScanDevices" -> handleStopScanDevices()
            "connectDevice" -> handleConnectDevice(address)
            "bindDevice" -> handleBindDevice(password, is24H)
            "disconnectDevice" -> handleDisconnectDevice()
            "isDeviceConnected" -> handleIsDeviceConnected()
            "startDetectHeart" -> handleStartDetectHeart()
            "stopDetectHeart" -> handleStopDetectHeart()
            else -> result.notImplemented()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun handleRequestBluetoothPermissions() {
        getBluetoothManager(result!!).requestBluetoothPermissions()
    }

    private fun handleScanDevices() {
        getBluetoothManager(result!!).scanDevices()
    }

    private fun handleStopScanDevices() {
        getBluetoothManager(result!!).stopScanDevices()
    }

    private fun handleConnectDevice(address: String?) {
        if (address != null) {
            getBluetoothManager(result!!).connectDevice(address)
        } else {
            result?.error("INVALID_ARGUMENT", "MAC address is required", null)
        }
    }

    private fun handleBindDevice(password: String?, is24H: Boolean?) {
        if (password != null && is24H != null) {
            getBluetoothManager(result!!).bindDevice(password, is24H) { status ->
                if (status != null) {
                    result?.success(status.name)
                } else {
                    result?.error("UNKNOWN_STATUS", "Binding status is null", null)
                }
            }
        } else {
            result?.error("INVALID_ARGUMENT", "Password and 24-hour mode are required", null)
        }
    }

    private fun handleDisconnectDevice() {
        getBluetoothManager(result!!).disconnectDevice()
    }

    private fun handleIsDeviceConnected() {
        result?.success(getBluetoothManager(result!!).isDeviceConnected())
    }

    private fun handleStartDetectHeart() {
        getHeartRateManager().startDetectHeart()
    }

    private fun handleStopDetectHeart() {
        getHeartRateManager().stopDetectHeart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String?>, grantResults: IntArray): Boolean {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                result?.success(null)
            } else {
                result?.error("PERMISSION_DENIED", "Permission denied by user", null)
            }

            result = null
            return true
        }

        return false
    }

    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    fun setScanBluetoothEventSink(eventSink: EventChannel.EventSink?) {
        this.scanBluetoothEventSink = eventSink
    }

    fun setDetectHeartEventSink(eventSink: EventChannel.EventSink?) {
        this.detectHeartEventSink = eventSink
    }

    fun setDetectSpohEventSink(eventSink: EventChannel.EventSink?) {
        this.detectSpohEventSink = eventSink
    }

    private fun getBluetoothManager(result: MethodChannel.Result): VPBluetoothManager {
        return VPBluetoothManager(deviceStorage, result, activity!!, scanBluetoothEventSink, vpManager)
    }

    private fun getHeartRateManager(): HeartRate {
        return HeartRate(detectHeartEventSink, vpManager)
    }
}