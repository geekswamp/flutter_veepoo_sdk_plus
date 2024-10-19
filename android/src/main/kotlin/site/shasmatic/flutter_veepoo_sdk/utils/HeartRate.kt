package site.shasmatic.flutter_veepoo_sdk.utils

import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IHeartDataListener
import com.veepoo.protocol.listener.data.IHeartWaringDataListener
import com.veepoo.protocol.model.datas.HeartData
import com.veepoo.protocol.model.datas.HeartWaringData
import com.veepoo.protocol.model.settings.HeartWaringSetting
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

    /**
     * Sets the heart rate warning values.
     *
     * @param high The high heart rate warning value.
     * @param low The low heart rate warning value.
     */
    fun settingHeartWarning(high: Int, low: Int, open: Boolean) {
        executeHeartRateOperation {
            vpManager.settingHeartWarning(writeResponseCallBack, heartWarningDataCallBack, heartWarningSetting(high, low, open))
        }
    }

    /**
     * Reads the heart rate warning values.
     */
    fun readHeartWarning() {
        executeHeartRateOperation {
            vpManager.readHeartWarning(writeResponseCallBack, heartWarningDataCallBack)
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

    private val heartWarningDataCallBack = object : IHeartWaringDataListener {
        override fun onHeartWaringDataChange(data: HeartWaringData?) {
            VPLogger.d("Heart warning data: $data")
        }
    }

    private fun heartWarningSetting(high: Int, low: Int, open: Boolean): HeartWaringSetting {
        return HeartWaringSetting(high, low, open)
    }
}