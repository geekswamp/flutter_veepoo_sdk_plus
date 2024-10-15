import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_veepoo_sdk/exceptions/device_connection_exception.dart';
import 'package:flutter_veepoo_sdk/exceptions/heart_detection_exception.dart';
import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';
import 'models/bluetooth_result.dart';
import 'models/heart_rate_result.dart';

/// {@template method_channel_flutter_veepoo_sdk}
/// An implementation of [FlutterVeepooSdkPlatform] that uses method channels.
/// {@endtemplate}
class MethodChannelFlutterVeepooSdk extends FlutterVeepooSdkPlatform {
  /// {@macro method_channel_flutter_veepoo_sdk}
  MethodChannelFlutterVeepooSdk();

  /// The name of the channel used to interact with the native platform.
  static const String _channelName = 'site.shasmatic.flutter_veepoo_sdk';

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final MethodChannel methodChannel =
      const MethodChannel('$_channelName/command');

  /// The event channel used to receive Bluetooth scan results.
  @visibleForTesting
  final EventChannel scanBluetoothEventChannel =
      const EventChannel('$_channelName/scan_bluetooth_event_channel');

  /// The event channel used to receive heart rate results.
  @visibleForTesting
  final EventChannel heartRateEventChannel =
      const EventChannel('$_channelName/detect_heart_event_channel');

  /// Requests the necessary permissions to use Bluetooth.
  @override
  Future<void> requestBluetoothPermissions() async {
    await methodChannel.invokeMethod<void>('requestBluetoothPermissions');
  }

  /// Scans Bluetooth devices.
  @override
  Future<void> scanDevices() async {
    await methodChannel.invokeMethod<void>('scanDevices');
  }

  /// Stop scan Bluetooth devices.
  @override
  Future<void> stopScanDevices() async {
    await methodChannel.invokeMethod<void>('stopScanDevices');
  }

  /// Connects to a Bluetooth device.
  @override
  Future<void> connectDevice(String address) async {
    try {
      await methodChannel
          .invokeMethod<void>('connectDevice', {'address': address});
    } catch (e) {
      throw DeviceConnectionException('Failed to connect to device: $e');
    }
  }

  /// Disconnects from a Bluetooth device.
  @override
  Future<void> disconnectDevice() async {
    try {
      await methodChannel.invokeMethod<void>('disconnectDevice');
    } catch (e) {
      throw DeviceConnectionException('Failed to disconnect from device: $e');
    }
  }

  /// Bind device with password and is24H.
  /// This function will return a [DeviceBindingStatus] to indicate the status of the device binding.
  @override
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) async {
    try {
      final String? result = await methodChannel.invokeMethod<String>(
        'bindDevice',
        {'password': password, 'is24H': is24H},
      );

      if (result != null) {
        return DeviceBindingStatus.fromString(result);
      }

      return null;
    } catch (e) {
      throw DeviceConnectionException('Failed to bind device: $e');
    }
  }

  /// Checks if a device is connected.
  @override
  Future<bool?> isDeviceConnected() async {
    return await methodChannel.invokeMethod<bool>('isDeviceConnected');
  }

  /// Start detect heart rate.
  /// This function is used to start detecting heart rate. The device will return the heart rate data to the app.
  ///
  /// Please use [bindDevice] before calling this function or you can use [startDetectHeartAfterBinding] to bind and start detect heart rate.
  @override
  Future<void> startDetectHeart() async {
    try {
      await methodChannel.invokeMethod<void>('startDetectHeart');
    } catch (e) {
      throw HeartDetectionException('Failed to start detect heart: $e');
    }
  }

  /// Start detect heart rate after binding.
  @override
  Future<void> startDetectHeartAfterBinding(String password, bool is24H) async {
    try {
      final DeviceBindingStatus? status = await bindDevice(password, is24H);

      if (status != null) {
        if (status == DeviceBindingStatus.checkAndTimeSuccess) {
          await startDetectHeart();
        }
      }
    } catch (e) {
      throw HeartDetectionException('Failed to start detect heart: $e');
    }
  }

  /// Stop detect heart rate.
  @override
  Future<void> stopDetectHeart() async {
    try {
      await methodChannel.invokeMethod<void>('stopDetectHeart');
    } catch (e) {
      throw HeartDetectionException('Failed to stop detect heart: $e');
    }
  }

  /// Stream of Bluetooth scan results.
  @override
  Stream<List<BluetoothResult>?> get scanBluetoothResult {
    return scanBluetoothEventChannel
        .receiveBroadcastStream()
        .map((dynamic event) {
      if (event is Map) {
        final result =
            event.map((key, value) => MapEntry(key.toString(), value));

        return [BluetoothResult.fromJson(result)];
      }
      return null;
    });
  }

  /// Stream of heart rate results.
  @override
  Stream<HeartRateResult?> get heartRateResult {
    return heartRateEventChannel.receiveBroadcastStream().map((dynamic event) {
      if (event is Map) {
        final result =
            event.map((key, value) => MapEntry(key.toString(), value));

        return HeartRateResult.fromJson(result);
      }
      return null;
    });
  }
}
