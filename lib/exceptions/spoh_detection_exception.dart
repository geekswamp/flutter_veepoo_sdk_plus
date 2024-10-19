/// {@template spoh_detection_exception}
/// An exception for spoh detection.
/// {@endtemplate}
class SpohDetectionException implements Exception {
  /// {@macro spoh_detection_exception}
  SpohDetectionException(this.message);

  /// The exception message.
  final String message;

  @override
  String toString() {
    return 'SpohDetectionException: $message';
  }
}
