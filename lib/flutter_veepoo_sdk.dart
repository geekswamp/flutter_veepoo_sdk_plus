library flutter_veepoo_sdk;

import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';
import 'models/bluetooth_result.dart';
import 'models/heart_rate_result.dart';

export 'exceptions/device_connection_exception.dart';
export 'exceptions/heart_detection_exception.dart';
export 'models/bluetooth_result.dart';
export 'models/heart_rate_result.dart';
export 'statuses/device_binding_statuses.dart';
export 'statuses/heart_statuses.dart';

/// {@template flutter_veepoo_sdk}
/// A Flutter plugin for Veepoo SDK.
/// {@endtemplate}
class FlutterVeepooSdk {
  /// {@macro flutter_veepoo_sdk}
  FlutterVeepooSdk();

  final FlutterVeepooSdkPlatform _platform = FlutterVeepooSdkPlatform.instance;

  /// Requests the necessary permissions to use Bluetooth.
  Future<void> requestBluetoothPermissions() {
    return _platform.requestBluetoothPermissions();
  }

  /// Scans Bluetooth devices.
  Future<void> scanDevices() {
    return _platform.scanDevices();
  }

  /// Connects to a Bluetooth device.
  Future<void> connectDevice(String address) {
    return _platform.connectDevice(address);
  }

  /// Disconnects from a Bluetooth device.
  Future<void> disconnectDevice() {
    return _platform.disconnectDevice();
  }

  /// Bind device with password and is24H.
  /// This function will return a [DeviceBindingStatus] to indicate the status of the device binding.
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) {
    return _platform.bindDevice(password, is24H);
  }

  /// Checks if a device is connected.
  Future<bool?> isDeviceConnected() {
    return _platform.isDeviceConnected();
  }

  /// Start detect heart rate.
  /// This function is used to start detecting heart rate. The device will return the heart rate data to the app.
  ///
  /// Please use [bindDevice] before calling this function or you can use [startDetectHeartAfterBinding] to bind and start detect heart rate.
  Future<void> startDetectHeart() {
    return _platform.startDetectHeart();
  }

  /// Start detect heart rate after binding.
  Future<void> startDetectHeartAfterBinding(String password, bool is24H) {
    return _platform.startDetectHeartAfterBinding(password, is24H);
  }

  /// Stop detect heart rate.
  Future<void> stopDetectHeart() {
    return _platform.stopDetectHeart();
  }

  /// Stream of Bluetooth scan results.
  Stream<List<BluetoothResult>?> get scanBluetoothResult {
    return _platform.scanBluetoothResult;
  }

  /// Stream of heart rate results.
  Stream<HeartRateResult?> get heartRateResult {
    return _platform.heartRateResult;
  }
}
