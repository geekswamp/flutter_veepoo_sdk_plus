import 'package:flutter/services.dart';
import 'package:flutter_veepoo_sdk/exceptions/device_connection_exception.dart';
import 'package:flutter_veepoo_sdk/exceptions/heart_detection_exception.dart';
import 'package:flutter_veepoo_sdk/exceptions/permission_exception.dart';
import 'package:flutter_veepoo_sdk/exceptions/unexpected_event_type_exception.dart';
import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';
import 'package:flutter_veepoo_sdk/statuses/permission_statuses.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';
import 'models/bluetooth_device.dart';
import 'models/heart_rate.dart';

/// {@template method_channel_flutter_veepoo_sdk}
/// An implementation of [FlutterVeepooSdkPlatform] that uses method channels.
/// {@endtemplate}
class MethodChannelFlutterVeepooSdk extends FlutterVeepooSdkPlatform {
  static const String _channelName = 'site.shasmatic.flutter_veepoo_sdk';

  final MethodChannel methodChannel =
      const MethodChannel('$_channelName/command');
  final EventChannel scanBluetoothEventChannel =
      const EventChannel('$_channelName/scan_bluetooth_event_channel');
  final EventChannel heartRateEventChannel =
      const EventChannel('$_channelName/detect_heart_event_channel');

  /// Requests Bluetooth permissions.
  ///
  /// Returns a [PermissionStatus] if the request is successful, otherwise null.
  /// Throws a [PermissionException] if the request fails.
  @override
  Future<PermissionStatus?> requestBluetoothPermissions() async {
    try {
      final String? status = await methodChannel
          .invokeMethod<String>('requestBluetoothPermissions');
      return status != null ? PermissionStatus.fromString(status) : null;
    } on PlatformException catch (e) {
      throw PermissionException('Failed to request permissions: $e');
    }
  }

  /// Starts scanning for Bluetooth devices.
  @override
  Future<void> scanDevices() async {
    await methodChannel.invokeMethod<void>('scanDevices');
  }

  /// Stops scanning for Bluetooth devices.
  @override
  Future<void> stopScanDevices() async {
    await methodChannel.invokeMethod<void>('stopScanDevices');
  }

  /// Connects to a Bluetooth device with the given [address].
  ///
  /// Throws a [DeviceConnectionException] if the connection fails.
  @override
  Future<void> connectDevice(String address) async {
    try {
      await methodChannel
          .invokeMethod<void>('connectDevice', {'address': address});
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to connect to device: $e');
    }
  }

  /// Disconnects from the currently connected Bluetooth device.
  ///
  /// Throws a [DeviceConnectionException] if the disconnection fails or if no device is connected.
  @override
  Future<void> disconnectDevice() async {
    try {
      if (await isDeviceConnected() == true) {
        await methodChannel.invokeMethod<void>('disconnectDevice');
      } else {
        throw DeviceConnectionException('Device is not connected');
      }
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to disconnect from device: $e');
    }
  }

  /// Gets the address of the currently connected Bluetooth device.
  ///
  /// Returns the address as a [String].
  /// Throws a [DeviceConnectionException] if the request fails.
  @override
  Future<String?> getAddress() async {
    try {
      return await methodChannel.invokeMethod<String>('getAddress');
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to get address: $e');
    }
  }

  /// Gets the current status of the Bluetooth device.
  ///
  /// Returns the status as an [int].
  /// Throws a [DeviceConnectionException] if the request fails.
  @override
  Future<int?> getCurrentStatus() async {
    try {
      return await methodChannel.invokeMethod<int>('getCurrentStatus');
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to get current status: $e');
    }
  }

  /// Checks if a Bluetooth device is currently connected.
  ///
  /// Returns [true] if a device is connected, otherwise [false].
  /// Throws a [DeviceConnectionException] if the request fails.
  @override
  Future<bool?> isDeviceConnected() async {
    try {
      return await methodChannel.invokeMethod<bool>('isDeviceConnected');
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to check device connection: $e');
    }
  }

  /// Binds a Bluetooth device with the given [password] and [is24H] flag.
  ///
  /// Returns a [DeviceBindingStatus] if the binding is successful, otherwise null.
  /// Throws a [DeviceConnectionException] if the binding fails or if no device is connected.
  @override
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) async {
    try {
      if (await isDeviceConnected() == true) {
        final String? result = await methodChannel.invokeMethod<String>(
          'bindDevice',
          {'password': password, 'is24H': is24H},
        );
        return result != null ? DeviceBindingStatus.fromString(result) : null;
      } else {
        throw DeviceConnectionException('Device is not connected');
      }
    } on PlatformException catch (e) {
      throw DeviceConnectionException('Failed to bind device: $e');
    }
  }

  /// Starts heart rate detection.
  ///
  /// Throws a [HeartDetectionException] if the detection fails or if no device is connected.
  ///
  /// You can alternatively use [startDetectHeartAfterBinding] to bind and start detection.
  @override
  Future<void> startDetectHeart() async {
    try {
      if (await isDeviceConnected() == true) {
        await methodChannel.invokeMethod<void>('startDetectHeart');
      } else {
        throw DeviceConnectionException('Device is not connected');
      }
    } on PlatformException catch (e) {
      throw HeartDetectionException('Failed to start detect heart: $e');
    }
  }

  /// Binds a device and starts heart rate detection with the given [password] and [is24H] flag.
  ///
  /// Throws a [HeartDetectionException] if the binding or detection fails.
  @override
  Future<void> startDetectHeartAfterBinding(String password, bool is24H) async {
    try {
      final DeviceBindingStatus? status = await bindDevice(password, is24H);
      if (status == DeviceBindingStatus.checkAndTimeSuccess) {
        await startDetectHeart();
      }
    } on PlatformException catch (e) {
      throw HeartDetectionException('Failed to start detect heart: $e');
    }
  }

  /// Stops heart rate detection.
  ///
  /// Throws a [HeartDetectionException] if the detection fails or if no device is connected.
  @override
  Future<void> stopDetectHeart() async {
    try {
      if (await isDeviceConnected() == true) {
        await methodChannel.invokeMethod<void>('stopDetectHeart');
      } else {
        throw DeviceConnectionException('Device is not connected');
      }
    } on PlatformException catch (e) {
      throw HeartDetectionException('Failed to stop detect heart: $e');
    }
  }

  /// Stream of Bluetooth scan results.
  ///
  /// Returns a [Stream] of [List] of [BluetoothDevice] objects.
  @override
  Stream<List<BluetoothDevice>?> get scanBluetoothResult {
    return scanBluetoothEventChannel.receiveBroadcastStream().map((event) {
      if (event is List) {
        return event
            .whereType<Map<String, dynamic>>()
            .map(BluetoothDevice.fromJson)
            .toList();
      } else if (event is Map<String, dynamic> ||
          event is Map<Object?, Object?>) {
        return [BluetoothDevice.fromJson(Map<String, dynamic>.from(event))];
      } else {
        throw UnexpectedEventTypeException('${event.runtimeType}');
      }
    });
  }

  /// Stream of heart rate detection results.
  ///
  /// Returns a [Stream] of [HeartRate] objects.
  @override
  Stream<HeartRate?> get heartRateResult {
    return heartRateEventChannel.receiveBroadcastStream().map((dynamic event) {
      if (event is Map) {
        final result =
            event.map((key, value) => MapEntry(key.toString(), value));

        return HeartRate.fromJson(result);
      }
      return null;
    });
  }
}
