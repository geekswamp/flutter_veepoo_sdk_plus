package site.shasmatic.flutter_veepoo_sdk.utils

import com.inuker.bluetooth.library.Constants
import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.listener.base.IBleWriteResponse
import com.veepoo.protocol.listener.data.IHeartDataListener
import com.veepoo.protocol.model.datas.HeartData
import io.flutter.plugin.common.EventChannel
import site.shasmatic.flutter_veepoo_sdk.VeepooLogger

/**
 * Utility class for sending heart rate events via an [EventChannel.EventSink].
 *
 * @constructor Creates a new [HeartRate] instance with the specified event sink, [VPOperateManager], and [BluetoothManager].
 * @param heartRateEventSink The sink that receives the heart rate events.
 * @param vpManager The [VPOperateManager] used to control device operations.
 * @param bluetoothManager The [BluetoothManager] used to handle Bluetooth operations.
 * @author Ahmad Rifa'i
 */
class HeartRate(
    private val heartRateEventSink: EventChannel.EventSink?,
    private val vpManager: VPOperateManager,
    private val bluetoothManager: BluetoothManager
) {

    private val sendEvent: SendEvent = SendEvent(heartRateEventSink)

    /**
     * Starts the heart rate detection process.
     *
     * This method is responsible for starting the heart rate detection process
     */
    fun startDetectHeart() {
        try {
            vpManager.startDetectHeart(writeResponseCallBack, heartDataListener)
        } catch (e: Exception) {
            VeepooLogger.e("Error starting heart rate detection: ${e.message}", e.cause)
        }
    }

    /**
     * Stops the heart rate detection process.
     *
     * This method is responsible for stopping the heart rate detection process
     */
    fun stopDetectHeart() {
        vpManager.stopDetectHeart(writeResponseCallBack)
    }

    private val writeResponseCallBack = object : IBleWriteResponse {
        override fun onResponse(status: Int) {
            if (status != Constants.REQUEST_SUCCESS) {
                VeepooLogger.d("Write response: $status")
            }
        }
    }

    private val heartDataListener = object : IHeartDataListener {
        override fun onDataChange(data: HeartData?) {
            var heartResult = mapOf<String, Any?>(
                "data" to data?.data,
                "status" to data?.heartStatus?.name
            )

            sendEvent.sendHeartRateEvent(heartResult)
        }
    }
}