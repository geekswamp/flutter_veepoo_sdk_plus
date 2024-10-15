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
import site.shasmatic.flutter_veepoo_sdk.utils.BluetoothManager
import site.shasmatic.flutter_veepoo_sdk.utils.DeviceStorage

/**
 * Handles method calls from Flutter to perform various operations related to Bluetooth management,
 * heart rate detection, and blood oxygen (SpO2) detection.
 *
 * @constructor Initializes the [MethodChannelHandler] with the given [VPOperateManager], [VpSpGetUtil], and [DeviceStorage].
 * @param vpManager An instance of [VPOperateManager] used to control operations on the wearable device.
 * @param vpSpGetUtil An instance of [VpSpGetUtil] used to access shared preferences for device settings.
 * @param deviceStorage An instance of [DeviceStorage] used for local storage interactions.
 * @author Ahmad Rifa'i
 */
class MethodChannelHandler(
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
        this.result = result
        when(call.method) {
            "requestBluetoothPermissions" -> getBluetoothManager(result).requestBluetoothPermissions()
            "scanDevices" -> getBluetoothManager(result).scanDevices()
            "stopScanDevices" -> getBluetoothManager(result).stopScanDevices()
            "disconnectDevice" -> getBluetoothManager(result).disconnectDevice()
            "connectDevice" -> {
                val address = call.argument<String>("address")
                if (address != null) {
                    getBluetoothManager(result).connectDevice(address)
                } else {
                    result.error("INVALID_ARGUMENT", "Address is required", null)
                }
            }
            else -> result.notImplemented()
        }
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

    /**
     * Sets the activity instance for this handler.
     *
     * @param activity The activity instance to be associated with this handler or null to clear the current activity.
     */
    fun setActivity(activity: Activity?) {
        this.activity = activity
    }

    /**
     * Sets the event sink for handling Bluetooth scan events.
     *
     * @param eventSink The EventSink instance to be used for Bluetooth scan events, or null to clear the current sink.
     */
    fun setScanBluetoothEventSink(eventSink: EventChannel.EventSink?) {
        this.scanBluetoothEventSink = eventSink
    }

    /**
     * Sets the event sink for handling heart detection events.
     *
     * @param eventSink The EventSink instance to be used for heart detection events, or null to clear the current sink.
     */
    fun setDetectHeartEventSink(eventSink: EventChannel.EventSink?) {
        this.detectHeartEventSink = eventSink
    }

    /**
     * Sets the event sink for handling SpO2 detection events.
     *
     * @param eventSink The EventSink instance to be used for SpO2 detection events, or null to clear the current sink.
     */
    fun setDetectSpohEventSink(eventSink: EventChannel.EventSink?) {
        this.detectSpohEventSink = eventSink
    }

    private fun getBluetoothManager(result: MethodChannel.Result): BluetoothManager {
        return BluetoothManager(deviceStorage, result, activity!!, scanBluetoothEventSink, vpManager)
    }
}