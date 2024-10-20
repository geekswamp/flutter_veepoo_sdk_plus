package site.shasmatic.flutter_veepoo_sdk.utils

import com.inuker.bluetooth.library.Code
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IBatteryDataListener
import com.veepoo.protocol.model.datas.BatteryData
import io.flutter.plugin.common.MethodChannel
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import java.lang.reflect.InvocationTargetException

/**
 * Utility class for reading the battery level of the connected device.
 *
 * @constructor Initializes the [Battery] with the given [MethodChannel.Result] and [VPOperateManager].
 * @param result The result object used to send the battery level data back to Flutter.
 * @param vpManager An instance of [VPOperateManager] used to control operations on the wearable device.
 */
class Battery(
    private val result: MethodChannel.Result,
    private val vpManager: VPOperateManager,
) {

    /**
     * Reads the battery level of the connected device.
     */
    fun readBattery() {
        try {
            vpManager.readBattery(writeResponseCallBack, batteryDataListener)
        } catch (e: InvocationTargetException) {
            throw VPException("Failed to read battery level: ${e.targetException.message}", e.targetException.cause)
        } catch (e: Exception) {
            throw VPException("Failed to read battery level: ${e.message}", e.cause)
        }
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            if (status != Code.REQUEST_SUCCESS) {
                VPLogger.e("Failed to read battery level: $status")
            }
        }
    }

    private val batteryDataListener = object : IBatteryDataListener {
        override fun onDataChange(data: BatteryData?) {
            val batteryResult = mapOf<String, Any?>(
                "level" to data?.batteryLevel,
                "percent" to data?.batteryPercent,
                "powerModel" to data?.powerModel,
                "state" to data?.state,
                "bat" to data?.bat,
                "isLow" to data?.isLowBattery,
                "isPercent" to data?.isPercent,
            )
            result.success(batteryResult)
        }
    }
}