part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.veepoo_exception}
/// Base class for all exceptions thrown by the Veepoo package.
/// {@endtemplate}
final class VeepooException implements Exception {
  /// {@macro flutter_veepoo_sdk.veepoo_exception}
  const VeepooException({this.message, this.details, this.stacktrace});

  /// The exception message.
  final String? message;

  /// The exception details.
  final String? details;

  /// The stack trace for the exception.
  final StackTrace? stacktrace;

  @override
  String toString() {
    return 'VeepooException($message, $details, $stacktrace)';
  }
}
