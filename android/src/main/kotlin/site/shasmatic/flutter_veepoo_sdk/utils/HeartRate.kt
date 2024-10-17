package site.shasmatic.flutter_veepoo_sdk.utils

import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IHeartDataListener
import com.veepoo.protocol.model.datas.HeartData
import io.flutter.plugin.common.EventChannel
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.exceptions.VPException
import java.lang.reflect.InvocationTargetException

/**
 * Utility class for sending heart rate events via an [EventChannel.EventSink].
 *
 * @constructor Creates a new [HeartRate] instance with the specified event sink, and [VPOperateManager].
 * @param heartRateEventSink The sink that receives the heart rate events.
 * @param vpManager The [VPOperateManager] used to control device operations.
 */
class HeartRate(
    private val heartRateEventSink: EventChannel.EventSink?,
    private val vpManager: VPOperateManager,
) {

    private val sendEvent: SendEvent = SendEvent(heartRateEventSink)

    /**
     * Starts the heart rate detection process.
     */
    fun startDetectHeart() {
        executeHeartRateOperation {
            vpManager.startDetectHeart(writeResponseCallBack, heartDataListener)
        }
    }

    /**
     * Stops the heart rate detection process.
     */
    fun stopDetectHeart() {
        executeHeartRateOperation {
            vpManager.stopDetectHeart(writeResponseCallBack)
        }
    }

    private fun executeHeartRateOperation(operation: () -> Unit) {
        try {
            operation()
        } catch (e: InvocationTargetException) {
            throw VPException("Error during heart rate operation: ${e.targetException.message}", e.targetException.cause)
        } catch (e: Exception) {
            throw VPException("Error during heart rate operation: ${e.message}", e.cause)
        }
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            VPLogger.d("Write response: $status")
        }
    }

    private val heartDataListener = object : IHeartDataListener {
        override fun onDataChange(data: HeartData?) {
            val heartResult = mapOf<String, Any?>(
                "data" to data?.data,
                "state" to data?.heartStatus?.name
            )
            sendEvent.sendHeartRateEvent(heartResult)
        }
    }
}