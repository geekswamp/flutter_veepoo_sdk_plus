package site.shasmatic.flutter_veepoo_sdk.utils

import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.ISpo2hDataListener
import com.veepoo.protocol.model.datas.Spo2hData
import com.veepoo.protocol.shareprence.VpSpGetUtil
import io.flutter.plugin.common.EventChannel
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import java.lang.reflect.InvocationTargetException

/**
 * Utility class for sending SpO2 events via an [EventChannel.EventSink].
 *
 * @constructor Creates a new [Spoh] instance with the specified event sink, [VpSpGetUtil], and [VPOperateManager].
 * @param spohEventSink The sink that receives the SpO2 events.
 * @param vpSpGetUtil The [VpSpGetUtil] used to access shared preferences for device settings.
 * @param vpManager The [VPOperateManager] used to control device operations.
 */
class Spoh(
    spohEventSink: EventChannel.EventSink?,
    private val vpSpGetUtil: VpSpGetUtil,
    private val vpManager: VPOperateManager,
) {

    private val sendEvent: SendEvent = SendEvent(spohEventSink)

    /**
     * Starts the SpO2 detection process.
     */
    fun startDetectSpoh() {
        executeSpohOperation {
            vpManager.startDetectSPO2H(writeResponseCallBack, spohDataListener)
        }
    }

    /**
     * Stops the SpO2 detection process.
     */
    fun stopDetectSpoh() {
        executeSpohOperation {
            vpManager.stopDetectSPO2H(writeResponseCallBack, spohDataListener)
        }
    }

    private fun executeSpohOperation(operation: () -> Unit) {
        try {
            if (vpSpGetUtil.isSupportSpo2h) {
                operation()
            } else {
                throw VPException("SpO2 detection is not supported on this device.")
            }
        } catch (e: InvocationTargetException) {
            throw VPException("Error during SpO2 operation: ${e.targetException.message}", e.targetException.cause)
        } catch (e: Exception) {
            throw VPException("Error during SpO2 operation: ${e.message}", e.cause)
        }
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            VPLogger.d("Write response: $status")
        }

    }

    private val spohDataListener = object : ISpo2hDataListener {
        override fun onSpO2HADataChange(data: Spo2hData?) {
            val spohResult = mapOf<String, Any?>(
                "spohStatus" to data?.spState?.name,
                "deviceStatus" to data?.deviceState?.name,
                "value" to data?.value,
                "checking" to data?.isChecking,
                "checkingProgress" to data?.checkingProgress,
                "rate" to data?.rateValue,
            )
            sendEvent.sendSpO2Event(spohResult)
        }
    }
}