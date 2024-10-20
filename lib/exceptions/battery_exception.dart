/// {@template battery_exception}
/// An exception thrown for the battery.
/// {@endtemplate}
class BatteryException implements Exception {
  /// {@macro battery_exception}
  BatteryException(this.message);

  /// The message of the exception.
  final String message;

  @override
  String toString() {
    return 'BatteryException: $message';
  }
}
