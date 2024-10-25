part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.heart_detection_exception}
/// Exception thrown when a heart detection fails.
/// {@endtemplate}
class HeartDetectionException implements Exception {
  /// {@macro flutter_veepoo_sdk.heart_detection_exception}
  HeartDetectionException(this.message);

  /// The error message.
  final String message;

  @override
  String toString() {
    return 'HeartDetectionException: $message';
  }
}
