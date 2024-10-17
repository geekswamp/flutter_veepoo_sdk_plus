/// {@template permission_status}
/// The status of a permission.
/// {@endtemplate}
enum PermissionStatus {
  /// The permission to access the requested feature is denied by the user.
  denied('PERMISSION_DENIED'),

  /// The permission to access the requested feature is granted by the user.
  granted('PERMISSION_GRANTED');

  /// {@macro permission_status}
  const PermissionStatus(this.statusValue);

  /// The value of the status.
  final String statusValue;

  /// Converts a string to a [PermissionStatus].
  static PermissionStatus fromString(String status) {
    return PermissionStatus.values.firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
