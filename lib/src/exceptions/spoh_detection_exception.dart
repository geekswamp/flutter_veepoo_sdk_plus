part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.spoh_detection_exception}
/// An exception for spoh detection.
/// {@endtemplate}
class SpohDetectionException implements Exception {
  /// {@macro flutter_veepoo_sdk.spoh_detection_exception}
  SpohDetectionException(this.message);

  /// The exception message.
  final String message;

  @override
  String toString() {
    return 'SpohDetectionException: $message';
  }
}
