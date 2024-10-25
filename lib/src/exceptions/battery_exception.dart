part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.battery_exception}
/// An exception thrown for the battery.
/// {@endtemplate}
class BatteryException implements Exception {
  /// {@macro flutter_veepoo_sdk.battery_exception}
  BatteryException(this.message);

  /// The message of the exception.
  final String message;

  @override
  String toString() {
    return 'BatteryException: $message';
  }
}
