library flutter_veepoo_sdk;

import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';
import 'package:flutter_veepoo_sdk/statuses/permission_statuses.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';
import 'models/bluetooth_device.dart';
import 'models/heart_rate.dart';

export 'exceptions/device_connection_exception.dart';
export 'exceptions/heart_detection_exception.dart';
export 'exceptions/permission_exception.dart';
export 'exceptions/unexpected_event_type_exception.dart';
export 'models/bluetooth_device.dart';
export 'models/heart_rate.dart';
export 'statuses/device_binding_statuses.dart';
export 'statuses/heart_statuses.dart';
export 'statuses/permission_statuses.dart';

/// {@template flutter_veepoo_sdk}
/// A Flutter plugin for Veepoo SDK.
/// {@endtemplate}
class FlutterVeepooSdk {
  /// {@macro flutter_veepoo_sdk}
  FlutterVeepooSdk();

  final FlutterVeepooSdkPlatform _platform = FlutterVeepooSdkPlatform.instance;

  /// Requests the necessary permissions to use Bluetooth.
  Future<PermissionStatus?> requestBluetoothPermissions() {
    return _platform.requestBluetoothPermissions();
  }

  /// Check if Bluetooth is enabled.
  Future<bool?> isBluetoothEnabled() {
    return _platform.isBluetoothEnabled();
  }

  /// Open Bluetooth.
  Future<void> openBluetooth() {
    return _platform.openBluetooth();
  }

  /// Close Bluetooth.
  Future<void> closeBluetooth() {
    return _platform.closeBluetooth();
  }

  /// Scans Bluetooth devices.
  Future<void> scanDevices() {
    return _platform.scanDevices();
  }

  /// Stop scan Bluetooth devices.
  Future<void> stopScanDevices() {
    return _platform.stopScanDevices();
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

  /// Get connected device address.
  Future<String?> getAddress() {
    return _platform.getAddress();
  }

  /// Get current status.
  Future<int?> getCurrentStatus() {
    return _platform.getCurrentStatus();
  }

  /// Check if the device is connected.
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
  Stream<List<BluetoothDevice>?> get scanBluetoothResult {
    return _platform.scanBluetoothResult;
  }

  /// Stream of heart rate results.
  Stream<HeartRate?> get heartRateResult {
    return _platform.heartRateResult;
  }
}
