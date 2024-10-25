part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.permission_exception}
/// Exception thrown when a permission is denied.
/// {@endtemplate}
class PermissionException implements Exception {
  /// {@macro flutter_veepoo_sdk.permission_exception}
  PermissionException(this.message);

  /// The error message.
  final String message;

  @override
  String toString() {
    return 'PermissionException: $message';
  }
}
