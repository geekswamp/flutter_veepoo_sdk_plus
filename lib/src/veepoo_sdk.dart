part of '../flutter_veepoo_sdk.dart';

/// A general class for Flutter Veepoo SDK.
///
/// Don't use this class directly, use [VeepooSDK.instance] instead.
class VeepooSDK {
  VeepooSDK._();

  /// The instance of [VeepooSDK].
  static final VeepooSDK instance = VeepooSDK._();

  final FlutterVeepooSdkPlatform _platform = FlutterVeepooSdkPlatform.instance;

  /// Requests the necessary permissions to use Bluetooth.
  Future<PermissionStatuses?> requestBluetoothPermissions() {
    try {
      return _platform.requestBluetoothPermissions();
    } on VeepooException {
      rethrow;
    }
  }

  /// Open app settings.
  Future<void> openAppSettings() {
    try {
      return _platform.openAppSettings();
    } on VeepooException {
      rethrow;
    }
  }

  /// Check if Bluetooth is enabled.
  Future<bool?> isBluetoothEnabled() {
    try {
      return _platform.isBluetoothEnabled();
    } on VeepooException {
      rethrow;
    }
  }

  /// Open Bluetooth.
  Future<void> openBluetooth() {
    try {
      return _platform.openBluetooth();
    } on VeepooException {
      rethrow;
    }
  }

  /// Close Bluetooth.
  Future<void> closeBluetooth() {
    try {
      return _platform.closeBluetooth();
    } on VeepooException {
      rethrow;
    }
  }

  /// Scans Bluetooth devices.
  Future<void> scanDevices() {
    try {
      return _platform.scanDevices();
    } on VeepooException {
      rethrow;
    }
  }

  /// Stop scan Bluetooth devices.
  Future<void> stopScanDevices() {
    try {
      return _platform.stopScanDevices();
    } on VeepooException {
      rethrow;
    }
  }

  /// Connects to a Bluetooth device, or you can simply use [connectAndBindDevice] to connect and bind device.
  Future<void> connectDevice(String address) {
    try {
      return _platform.connectDevice(address);
    } on VeepooException {
      rethrow;
    }
  }

  /// Disconnects from a Bluetooth device.
  Future<void> disconnectDevice() {
    try {
      return _platform.disconnectDevice();
    } on VeepooException {
      rethrow;
    }
  }

  /// Bind device with password and is24H. This function can be used after successfully connecting to the device.
  /// This function will return a [DeviceBindingStatus] to indicate the status of the device binding.
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) {
    try {
      return _platform.bindDevice(password, is24H);
    } on VeepooException {
      rethrow;
    }
  }

  /// Get connected device address.
  Future<String?> getAddress() {
    try {
      return _platform.getAddress();
    } on VeepooException {
      rethrow;
    }
  }

  /// Get current status.
  Future<int?> getCurrentStatus() {
    try {
      return _platform.getCurrentStatus();
    } on VeepooException {
      rethrow;
    }
  }

  /// Check if the device is connected.
  Future<bool?> isDeviceConnected() {
    try {
      return _platform.isDeviceConnected();
    } on VeepooException {
      rethrow;
    }
  }

  /// Start detect heart rate.
  /// This function is used to start detecting heart rate. The device will return the heart rate data to the app.
  ///
  /// Please use [bindDevice] before calling this function or you can use [startDetectHeartAfterBinding] to bind and start detect heart rate.
  Future<void> startDetectHeart() {
    try {
      return _platform.startDetectHeart();
    } on VeepooException {
      rethrow;
    }
  }

  /// Start detect heart rate after binding.
  Future<void> startDetectHeartAfterBinding(String password, bool is24H) {
    try {
      return _platform.startDetectHeartAfterBinding(password, is24H);
    } on VeepooException {
      rethrow;
    }
  }

  /// Stop detect heart rate.
  Future<void> stopDetectHeart() {
    try {
      return _platform.stopDetectHeart();
    } on VeepooException {
      rethrow;
    }
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
    try {
      return _platform.startDetectSpoh();
    } on VeepooException {
      rethrow;
    }
  }

  /// Start detect SPOH (blood oxygen) after binding.
  Future<void> startDetectSpohAfterBinding(String password, bool is24H) {
    try {
      return _platform.startDetectSpohAfterBinding(password, is24H);
    } on VeepooException {
      rethrow;
    }
  }

  /// Stop detect SPOH (blood oxygen).
  Future<void> stopDetectSpoh() {
    try {
      return _platform.stopDetectSpoh();
    } on VeepooException {
      rethrow;
    }
  }

  /// Read battery level.
  Future<Battery?> readBattery() {
    try {
      return _platform.readBattery();
    } on VeepooException {
      rethrow;
    }
  }

  /// Stream of Bluetooth scan results.
  Stream<List<BluetoothDevice>?> get scanBluetoothDevices {
    try {
      return _platform.bluetoothDevices;
    } on VeepooException {
      rethrow;
    }
  }

  /// Stream of heart rate results.
  Stream<HeartRate?> get heartRate {
    try {
      return _platform.heartRate;
    } on VeepooException {
      rethrow;
    }
  }

  /// Stream of SPOH (blood oxygen) results.
  Stream<Spoh?> get spoh {
    try {
      return _platform.spoh;
    } on VeepooException {
      rethrow;
    }
  }
}
