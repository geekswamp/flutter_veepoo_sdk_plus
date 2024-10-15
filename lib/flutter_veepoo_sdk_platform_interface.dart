import 'package:flutter_veepoo_sdk/models/heart_rate_result.dart';
import 'package:flutter_veepoo_sdk/statuses/device_binding_statuses.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_veepoo_sdk_method_channel.dart';
import 'models/bluetooth_result.dart';

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
  Future<void> requestBluetoothPermissions() {
    throw UnimplementedError(
      'requestBluetoothPermissions() has not been implemented.',
    );
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

  /// Checks if a device is connected.
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

  /// Stream of Bluetooth scan results.
  Stream<List<BluetoothResult>?> get scanBluetoothResult {
    throw UnimplementedError(
      'scanBluetoothEventChannel has not been implemented.',
    );
  }

  /// Stream of heart rate results.
  Stream<HeartRateResult?> get heartRateResult {
    throw UnimplementedError('heartRateEventChannel has not been implemented.');
  }
}