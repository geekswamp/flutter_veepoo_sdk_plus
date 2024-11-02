part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.permission_status}
/// The status of a permission.
/// {@endtemplate}
enum PermissionStatuses {
  /// The permission to access the requested feature is denied by the user.
  denied('DENIED'),

  /// The permission to access the requested feature is granted by the user.
  granted('GRANTED'),

  /// The permission to access the requested feature is permanently denied by the user.
  permanentlyDenied('PERMANENTLY_DENIED'),

  /// The permission to access the requested feature is restricted by the user.
  restricted('RESTRICTED'),

  /// The permission to access the requested feature is unknown.
  unknown('UNKNOWN');

  /// {@macro flutter_veepoo_sdk.permission_status}
  const PermissionStatuses(this.statusValue);

  /// The value of the status.
  final String statusValue;

  /// Converts a string to a [PermissionStatuses].
  factory PermissionStatuses.fromString(String status) {
    return PermissionStatuses.values.firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
