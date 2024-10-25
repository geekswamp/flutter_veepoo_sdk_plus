part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.device_connection_exception}
/// Exception thrown when a device connection fails.
/// {@endtemplate}
class DeviceConnectionException implements Exception {
  /// {@macro flutter_veepoo_sdk.device_connection_exception}
  DeviceConnectionException(this.message);

  /// The error message.
  final String message;

  @override
  String toString() {
    return 'DeviceConnectionException: $message';
  }
}
