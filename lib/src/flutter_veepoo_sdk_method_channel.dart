part of '../flutter_veepoo_sdk.dart';

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
  final EventChannel spohEventChannel =
      const EventChannel('$_channelName/detect_spoh_event_channel');

  /// Requests Bluetooth permissions.
  ///
  /// Returns a [PermissionStatuses] if the request is successful, otherwise null.
  /// Throws a [PermissionException] if the request fails.
  @override
  Future<PermissionStatuses?> requestBluetoothPermissions() async {
    try {
      final String? status = await methodChannel
          .invokeMethod<String>('requestBluetoothPermissions');
      return status != null ? PermissionStatuses.fromString(status) : null;
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to request permission: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Open app settings.
  @override
  Future<void> openAppSettings() async {
    try {
      await methodChannel.invokeMethod<void>('openAppSettings');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to open app settings: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Checks if Bluetooth is enabled.
  @override
  Future<bool?> isBluetoothEnabled() async {
    final bool? isEnabled =
        await methodChannel.invokeMethod<bool>('isBluetoothEnabled');
    return isEnabled;
  }

  /// Opens Bluetooth.
  @override
  Future<void> openBluetooth() async {
    try {
      await methodChannel.invokeMethod<void>('openBluetooth');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to open Bluetooth: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Closes Bluetooth.
  @override
  Future<void> closeBluetooth() async {
    try {
      await methodChannel.invokeMethod<void>('closeBluetooth');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to close Bluetooth: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Starts scanning for Bluetooth devices.
  @override
  Future<void> scanDevices() async {
    try {
      await methodChannel.invokeMethod<void>('scanDevices');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to scan devices: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Stops scanning for Bluetooth devices.
  @override
  Future<void> stopScanDevices() async {
    try {
      await methodChannel.invokeMethod<void>('stopScanDevices');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to stop scan devices: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Connects to a Bluetooth device with the given [address].
  ///
  /// Throws a [DeviceConnectionException] if the connection fails.
  @override
  Future<void> connectDevice(String address) async {
    try {
      await methodChannel.invokeMethod<void>(
        'connectDevice',
        {'address': address},
      );
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to connect address $address: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
        throw const VeepooException(message: 'Device is not connected');
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to disconnect from device: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to get address: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to get current status: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to check device connection: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Binds a Bluetooth device with the given [password] and [is24H] flag.
  ///
  /// Returns a [DeviceBindingStatus] if the binding is successful, otherwise null.
  /// Throws a [DeviceConnectionException] if the binding fails or if no device is connected.
  @override
  Future<DeviceBindingStatus?> bindDevice(String password, bool is24H) async {
    try {
      final String? result = await methodChannel.invokeMethod<String>(
        'bindDevice',
        {'password': password, 'is24H': is24H},
      );

      return result != null ? DeviceBindingStatus.fromString(result) : null;
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to bind device: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Starts heart rate detection.
  /// You can alternatively use [startDetectHeartAfterBinding] to bind and start detection.
  ///
  /// Throws a [HeartDetectionException] if the detection fails or if no device is connected.
  @override
  Future<void> startDetectHeart() async {
    try {
      if (await isDeviceConnected() == true) {
        await methodChannel.invokeMethod<void>('startDetectHeart');
      } else {
        throw const VeepooException(message: 'Device is not connected');
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to start detect heart: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to start detect heart: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
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
        throw const VeepooException(message: 'Device is not connected');
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to stop detect heart: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Setting heart rate warning.
  @override
  Future<void> settingHeartWarning(int high, int low, bool open) async {
    await methodChannel.invokeMethod<void>(
      'settingHeartWarning',
      {'high': high, 'low': low, 'open': open},
    );
  }

  /// Read heart rate warning.
  @override
  Future<void> readHeartWarning() async {
    await methodChannel.invokeMethod<void>('readHeartWarning');
  }

  /// Start detect blood oxygen.
  /// You can alternatively use [startDetectSpohAfterBinding] to bind and start detection.
  ///
  /// Throws a [SpohDetectionException] if the detection fails.
  @override
  Future<void> startDetectSpoh() async {
    try {
      await methodChannel.invokeMethod<void>('startDetectSpoh');
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to start detect blood oxygen: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Start detect blood oxygen after binding
  @override
  Future<void> startDetectSpohAfterBinding(String password, bool is24H) async {
    try {
      final DeviceBindingStatus? status = await bindDevice(password, is24H);
      if (status == DeviceBindingStatus.checkAndTimeSuccess) {
        await startDetectSpoh();
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to start detect blood oxygen: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Stop detect blood oxygen
  @override
  Future<void> stopDetectSpoh() async {
    try {
      if (await isDeviceConnected() == true) {
        await methodChannel.invokeMethod<void>('stopDetectSpoh');
      } else {
        throw const VeepooException(message: 'Device is not connected');
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to stop detect blood oxygen: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Read battery level.
  @override
  Future<Battery?> readBattery() async {
    try {
      if (await isDeviceConnected() == true) {
        final result =
            await methodChannel.invokeMapMethod<String, dynamic>('readBattery');
        return result != null ? Battery.fromJson(result) : null;
      } else {
        // throw DeviceConnectionException('Device is not connected');
        throw const VeepooException(message: 'Device is not connected');
      }
    } on PlatformException catch (error, stackTrace) {
      throw VeepooException(
        message: 'Failed to read battery: ${error.message}',
        details: error.details,
        stacktrace: stackTrace,
      );
    }
  }

  /// Stream of Bluetooth scan results.
  ///
  /// Returns a [Stream] of [List] of [BluetoothDevice] objects.
  @override
  Stream<List<BluetoothDevice>> get scanBluetoothDevices {
    return scanBluetoothEventChannel.receiveBroadcastStream().map((event) {
      if (event == null) return [];

      if (event is List) {
        return event.map((item) {
          if (item is Map<Object?, Object?>) {
            final convertedMap = Map<String, dynamic>.from(
              item.map((key, value) => MapEntry(key.toString(), value)),
            );
            return BluetoothDevice.fromJson(convertedMap);
          }
          throw VeepooException(
            message: 'Unexpected event type: ${item.runtimeType}',
          );
        }).toList();
      }

      throw VeepooException(
        message: 'Unexpected event type: ${event.runtimeType}',
      );
    });
  }

  /// Stream of heart rate detection results.
  ///
  /// Returns a [Stream] of [HeartRate] objects.
  @override
  Stream<HeartRate?> get heartRate {
    return heartRateEventChannel.receiveBroadcastStream().map((dynamic event) {
      if (event is Map) {
        final result =
            event.map((key, value) => MapEntry(key.toString(), value));

        return HeartRate.fromJson(result);
      } else {
        throw VeepooException(
          message: 'Unexpected event type: ${event.runtimeType}',
        );
      }
    });
  }

  /// Stream of blood oxygen results.
  ///
  /// Returns a [Stream] of [Spoh] objects.
  @override
  Stream<Spoh?> get spoh {
    return spohEventChannel.receiveBroadcastStream().map((dynamic event) {
      if (event is Map<Object?, Object?>) {
        final result =
            event.map((key, value) => MapEntry(key.toString(), value));

        return Spoh.fromJson(result);
      } else {
        throw VeepooException(
          message: 'Unexpected event type: ${event.runtimeType}',
        );
      }
    });
  }
}
