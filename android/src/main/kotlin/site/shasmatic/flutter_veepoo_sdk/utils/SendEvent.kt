package site.shasmatic.flutter_veepoo_sdk.utils

import io.flutter.plugin.common.EventChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class for sending various types of events, such as Bluetooth, heart rate, heart warning,
 * and SpO2 events, via an [EventChannel.EventSink].
 *
 * @constructor Initializes the [SendEvent] with the given [EventChannel.EventSink].
 * @param eventSink The sink that receives the events.
 * @author Ahmad Rifa'i
 */
class SendEvent(private val eventSink: EventChannel.EventSink?) {

    /**
     * Sends a Bluetooth event with the given scan result data.
     *
     * This method is responsible for sending Bluetooth scan result events
     * to the [EventChannel.EventSink]. The event data is provided as a
     * map containing key-value pairs representing the scan result.
     *
     * @param scanResult A map containing the Bluetooth scan result data.
     */
    fun sendBluetoothEvent(scanResult: Map<String, Any>) {
        sendEvent(scanResult)
    }

    /**
     * Sends a heart rate event with the given heart rate data.
     *
     * This method is responsible for sending heart rate events to the
     * [EventChannel.EventSink]. The event data is provided as a map
     * containing key-value pairs representing the heart rate data.
     *
     * @param heartRateData A map containing the heart rate data.
     */
    fun sendHeartRateEvent(heartRateData: Map<String, Any?>) {
        sendEvent(heartRateData)
    }

    /**
     * Sends a heart warning event with the given heart warning data.
     *
     * This method is responsible for sending heart warning events to the
     * [EventChannel.EventSink]. The event data is provided as a map
     * containing key-value pairs representing the heart warning data.
     *
     * @param heartWarningData A map containing the heart warning data.
     */
    fun sendHeartWarningEvent(heartWarningData: Map<String, Any>) {
        sendEvent(heartWarningData)
    }

    /**
     * Sends a SpO2 event with the given SpO2 data.
     *
     * This method is responsible for sending SpO2 events to the
     * [EventChannel.EventSink]. The event data is provided as a map
     * containing key-value pairs representing the SpO2 data.
     *
     * @param spO2Data A map containing the SpO2 data.
     */
    fun sendSpO2Event(spO2Data: Any) {
        sendEvent(spO2Data)
    }

    private fun sendEvent(eventData: Any) {
        CoroutineScope(Dispatchers.Main).launch {
            eventSink?.success(eventData)
        }
    }
}