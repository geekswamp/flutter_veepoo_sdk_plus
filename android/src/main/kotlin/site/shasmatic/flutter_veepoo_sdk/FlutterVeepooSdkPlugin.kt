package site.shasmatic.flutter_veepoo_sdk

import com.veepoo.protocol.VPOperateManager
import com.veepoo.protocol.shareprence.VpSpGetUtil
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import site.shasmatic.flutter_veepoo_sdk.utils.DeviceStorage

/**
 * Flutter plugin for integrating with Veepoo SDK on Android.
 *
 * This class implements the [FlutterPlugin] and [ActivityAware] interfaces to facilitate
 * communication between Flutter and the Veepoo SDK.
 */
class FlutterVeepooSdkPlugin: FlutterPlugin, ActivityAware {

  private lateinit var channel : MethodChannel
  private lateinit var methodChannelHandler: VPMethodChannelHandler
  private lateinit var deviceStorage: DeviceStorage
  private lateinit var vpSpGetUtil: VpSpGetUtil
  private var vpManager: VPOperateManager? = null

  init {
      vpManager = VPOperateManager.getInstance()
  }

  companion object {
      private const val CHANNEL = "site.shasmatic.flutter_veepoo_sdk"
      private const val COMMAND_CHANNEL = "$CHANNEL/command"
      private const val SCAN_BLUETOOTH_EVENT_CHANNEL = "$CHANNEL/scan_bluetooth_event_channel"
      private const val DETECT_HEART_EVENT_CHANNEL = "$CHANNEL/detect_heart_event_channel"
      private const val DETECT_SPOH_EVENT_CHANNEL = "$CHANNEL/detect_spoh_event_channel"
  }

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    deviceStorage = DeviceStorage(flutterPluginBinding.applicationContext)
    vpManager?.init(flutterPluginBinding.applicationContext)
    vpSpGetUtil = VpSpGetUtil.getVpSpVariInstance(flutterPluginBinding.applicationContext)
    initializeChannels(flutterPluginBinding.binaryMessenger)
  }

  private fun initializeChannels(messenger: BinaryMessenger) {
    methodChannelHandler = VPMethodChannelHandler(vpManager!!, vpSpGetUtil, deviceStorage)
    channel = MethodChannel(messenger, COMMAND_CHANNEL)
    channel.setMethodCallHandler(methodChannelHandler)

    setupEventChannels(messenger, SCAN_BLUETOOTH_EVENT_CHANNEL) { methodChannelHandler.setScanBluetoothEventSink(it) }
    setupEventChannels(messenger, DETECT_HEART_EVENT_CHANNEL) { methodChannelHandler.setDetectHeartEventSink(it) }
    setupEventChannels(messenger, DETECT_SPOH_EVENT_CHANNEL) { methodChannelHandler.setDetectSpohEventSink(it) }
  }

  private fun stopChannels() {
    channel.setMethodCallHandler(null)
  }

  private fun setupEventChannels(messenger: BinaryMessenger, channelName: String, eventHandler: (EventChannel.EventSink?) -> Unit) {
    EventChannel(messenger, channelName).setStreamHandler(
        object : EventChannel.StreamHandler {
          override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
            eventHandler(events)
          }

          override fun onCancel(arguments: Any?) {
            eventHandler(null)
          }
        }
    )
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    stopChannels()
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    methodChannelHandler.setActivity(binding.activity)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivity() {
    methodChannelHandler.setActivity(null)
  }
}
