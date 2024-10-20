/// {@template spoh_statuses}
/// An enum for spoh statuses.
/// {@endtemplate}
enum SpohStatuses {
  /// The status when the spoh is open.
  open('OPEN'),

  /// The status when the spoh is close.
  close('CLOSE'),

  /// The status when the spoh is not supported.
  notSupported('NOT_SUPPORT'),

  /// The status when the spoh is unknown.
  unknown('UNKONW');

  /// {@macro spoh_statuses}
  const SpohStatuses(this.statusValue);

  /// The value of the status.
  final String statusValue;

  /// Converts a string to a [SpohStatuses].
  static SpohStatuses fromString(String status) {
    return SpohStatuses.values.firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
