library flutter_veepoo_sdk;

import 'package:flutter_veepoo_sdk/enums/device_binding_statuses.dart';
import 'package:flutter_veepoo_sdk/enums/permission_statuses.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';
import 'models/battery.dart';
import 'models/bluetooth_device.dart';
import 'models/heart_rate.dart';
import 'models/spoh.dart';

export 'exceptions/battery_exception.dart';
export 'exceptions/device_connection_exception.dart';
export 'exceptions/heart_detection_exception.dart';
export 'exceptions/permission_exception.dart';
export 'exceptions/spoh_detection_exception.dart';
export 'exceptions/unexpected_event_type_exception.dart';
export 'models/battery.dart';
export 'models/bluetooth_device.dart';
export 'models/heart_rate.dart';
export 'models/spoh.dart';
export 'enums/battery_levels.dart';
export 'enums/battery_states.dart';
export 'enums/device_binding_statuses.dart';
export 'enums/device_statuses.dart';
export 'enums/heart_statuses.dart';
export 'enums/permission_statuses.dart';
export 'enums/power_statuses.dart';
export 'enums/spoh_statuses.dart';

/// {@template flutter_veepoo_sdk}
/// A Flutter plugin for Veepoo SDK.
/// {@endtemplate}
class FlutterVeepooSdk {
  FlutterVeepooSdk._();

  static final FlutterVeepooSdk _instance = FlutterVeepooSdk._();

  factory FlutterVeepooSdk() => _instance;

  /// {@macro flutter_veepoo_sdk}
  static FlutterVeepooSdk get instance => _instance;

  final FlutterVeepooSdkPlatform _platform = FlutterVeepooSdkPlatform.instance;

  /// Requests the necessary permissions to use Bluetooth.
  Future<PermissionStatuses?> requestBluetoothPermissions() {
    return _platform.requestBluetoothPermissions();
  }

  /// Open app settings.
  Future<void> openAppSettings() {
    return _platform.openAppSettings();
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

  /// Connects to a Bluetooth device, or you can simply use [connectAndBindDevice] to connect and bind device.
  Future<void> connectDevice(String address) {
    return _platform.connectDevice(address);
  }

  /// Disconnects from a Bluetooth device.
  Future<void> disconnectDevice() {
    return _platform.disconnectDevice();
  }

  /// Bind device with password and is24H. This function can be used after successfully connecting to the device.
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

  /// Setting heart rate warning.
  Future<void> settingHeartRate(int high, int low, bool open) {
    return _platform.settingHeartWarning(high, low, open);
  }

  /// Read heart rate warning.
  Future<void> readHeartRate() {
    return _platform.readHeartWarning();
  }

  /// Start detect SPOH (blood oxygen).
  /// This function is used to start detecting SPOH (blood oxygen). The device will return the SPOH data to the app.
  /// Please use [bindDevice] before calling this function or you can use [startDetectSpohAfterBinding] to bind and start detect SPOH.
  Future<void> startDetectSpoh() {
    return _platform.startDetectSpoh();
  }

  /// Start detect SPOH (blood oxygen) after binding.
  Future<void> startDetectSpohAfterBinding(String password, bool is24H) {
    return _platform.startDetectSpohAfterBinding(password, is24H);
  }

  /// Stop detect SPOH (blood oxygen).
  Future<void> stopDetectSpoh() {
    return _platform.stopDetectSpoh();
  }

  /// Read battery level.
  Future<Battery?> readBattery() {
    return _platform.readBattery();
  }

  /// Stream of Bluetooth scan results.
  Stream<List<BluetoothDevice>?> get scanBluetoothDevices {
    return _platform.scanBluetoothDevices;
  }

  /// Stream of heart rate results.
  Stream<HeartRate?> get heartRate {
    return _platform.heartRate;
  }

  /// Stream of SPOH (blood oxygen) results.
  Stream<Spoh?> get spoh {
    return _platform.spoh;
  }
}
