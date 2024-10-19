import 'package:flutter_veepoo_sdk/models/spoh.dart';
import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';
import 'package:flutter_veepoo_sdk/statuses/permission_statuses.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_veepoo_sdk_method_channel.dart';
import 'models/bluetooth_device.dart';
import 'models/heart_rate.dart';

/// The interface that implementations of flutter_veepoo_sdk must implement.
abstract class FlutterVeepooSdkPlatform extends PlatformInterface {
  /// Constructs a FlutterVeepooSdkPlatform.
  FlutterVeepooSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterVeepooSdkPlatform _instance = MethodChannelFlutterVeepooSdk();

  /// The default instance of [FlutterVeepooSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterVeepooSdk].
  static FlutterVeepooSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterVeepooSdkPlatform] when
  /// they register themselves.
  static set instance(FlutterVeepooSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Requests the necessary permissions to use Bluetooth.
  Future<PermissionStatuses?> requestBluetoothPermissions() {
    throw UnimplementedError(
      'requestBluetoothPermissions() has not been implemented.',
    );
  }

  /// Open app settings.
  Future<void> openAppSettings() {
    throw UnimplementedError(
      'openAppSettings() has not been implemented.',
    );
  }

  /// Check if Bluetooth is enabled.
  Future<bool?> isBluetoothEnabled() {
    throw UnimplementedError(
      'isBluetoothEnabled() has not been implemented.',
    );
  }

  /// Open Bluetooth.
  Future<void> openBluetooth() {
    throw UnimplementedError('openBluetooth() has not been implemented.');
  }

  /// Close Bluetooth.
  Future<void> closeBluetooth() {
    throw UnimplementedError('closeBluetooth() has not been implemented.');
  }

  /// Scans Bluetooth devices.
  Future<void> scanDevices() {
    throw UnimplementedError('scanDevices() has not been implemented.');
  }

  /// Stop scan Bluetooth devices.
  Future<void> stopScanDevices() {
    throw UnimplementedError('stopScanDevices() has not been implemented.');
  }

  /// Connects to a Bluetooth device.
  Future<void> connectDevice(String address) {
    throw UnimplementedError('connectDevice() has not been implemented.');
  }

  /// Bind device with password and is24H.
  /// This function will return a [DeviceBindingStatus] to indicate the status of the device binding.
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) {
    throw UnimplementedError('bindDevice() has not been implemented.');
  }

  /// Disconnects from a Bluetooth device.
  Future<void> disconnectDevice() {
    throw UnimplementedError('disconnectDevice() has not been implemented.');
  }

  /// Get the address of the connected device.
  Future<String?> getAddress() {
    throw UnimplementedError('getAddress() has not been implemented.');
  }

  /// Get the current status of the device.
  Future<int?> getCurrentStatus() {
    throw UnimplementedError('getCurrentStatus() has not been implemented.');
  }

  /// Check if the device is connected.
  Future<bool?> isDeviceConnected() {
    throw UnimplementedError('isDeviceConnected() has not been implemented.');
  }

  /// Start detect heart rate.
  /// This function is used to start detecting heart rate. The device will return the heart rate data to the app.
  ///
  /// Please use [bindDevice] before calling this function or you can use [startDetectHeartAfterBinding] to bind and start detect heart rate.
  Future<void> startDetectHeart() {
    throw UnimplementedError('startDetectHeart() has not been implemented.');
  }

  /// Start detect heart rate after binding.
  Future<void> startDetectHeartAfterBinding(String password, bool is24H) {
    throw UnimplementedError(
      'startDetectHeartAfterBinding() has not been implemented.',
    );
  }

  /// Stop detect heart rate.
  Future<void> stopDetectHeart() {
    throw UnimplementedError('stopDetectHeart() has not been implemented.');
  }

  /// Setting heart rate warning.
  Future<void> settingHeartWarning(int high, int low, bool open) {
    throw UnimplementedError('settingHeartWarning() has not been implemented.');
  }

  /// Read heart rate warning.
  Future<void> readHeartWarning() {
    throw UnimplementedError('readHeartWarning() has not been implemented.');
  }

  /// Start detect blood oxygen.
  Future<void> startDetectSpoh() {
    throw UnimplementedError('startDetectSpoh() has not been implemented.');
  }

  /// Start detect blood oxygen after binding.
  Future<void> startDetectSpohAfterBinding(String password, bool is24H) {
    throw UnimplementedError(
      'startDetectSpohAfterBinding() has not been implemented.',
    );
  }

  /// Stop detect blood oxygen.
  Future<void> stopDetectSpoh() {
    throw UnimplementedError('stopDetectSpoh() has not been implemented.');
  }

  /// Stream of Bluetooth scan results.
  Stream<List<BluetoothDevice>?> get scanBluetoothDevices {
    throw UnimplementedError(
      'scanBluetoothEventChannel has not been implemented.',
    );
  }

  /// Stream of heart rate results.
  Stream<HeartRate?> get heartRate {
    throw UnimplementedError('heartRateEventChannel has not been implemented.');
  }

  /// Stream of blood oxygen results.
  Stream<Spoh?> get spoh {
    throw UnimplementedError('spohEventChannel has not been implemented.');
  }
}
