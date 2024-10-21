package site.shasmatic.flutter_veepoo_sdk.utils

import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.data.IHeartDataListener
import com.veepoo.protocol.listener.data.IHeartWaringDataListener
import com.veepoo.protocol.model.settings.HeartWaringSetting
import io.flutter.plugin.common.EventChannel
import site.shasmatic.flutter_veepoo_sdk.VPLogger
import site.shasmatic.flutter_veepoo_sdk.VPWriteResponse
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
    private val writeResponse: VPWriteResponse = VPWriteResponse()

    /**
     * Starts the heart rate detection process.
     */
    fun startDetectHeart() {
        executeHeartRateOperation {
            vpManager.startDetectHeart(writeResponse, heartDataListener)
        }
    }

    /**
     * Stops the heart rate detection process.
     */
    fun stopDetectHeart() {
        executeHeartRateOperation {
            vpManager.stopDetectHeart(writeResponse)
        }
    }

    /**
     * Sets the heart rate warning values.
     *
     * @param high The high heart rate warning value.
     * @param low The low heart rate warning value.
     */
    fun settingHeartWarning(high: Int, low: Int, open: Boolean) {
        executeHeartRateOperation {
            vpManager.settingHeartWarning(writeResponse, heartWarningDataCallBack, heartWarningSetting(high, low, open))
        }
    }

    /**
     * Reads the heart rate warning values.
     */
    fun readHeartWarning() {
        executeHeartRateOperation {
            vpManager.readHeartWarning(writeResponse, heartWarningDataCallBack)
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

//    private val writeResponseCallBack = IBleWriteResponse { status -> VPLogger.d("Write response: $status") }

    private val heartDataListener = IHeartDataListener { data ->
        val heartResult = mapOf<String, Any?>(
            "data" to data?.data,
            "state" to data?.heartStatus?.name
        )
        sendEvent.sendHeartRateEvent(heartResult)
    }

    private val heartWarningDataCallBack = IHeartWaringDataListener { data -> VPLogger.d("Heart warning data: $data") }

    private fun heartWarningSetting(high: Int, low: Int, open: Boolean): HeartWaringSetting {
        return HeartWaringSetting(high, low, open)
    }
}