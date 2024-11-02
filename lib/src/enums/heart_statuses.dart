part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.heart_statuses}
/// An enum for heart statuses.
/// {@endtemplate}
enum HeartStatuses {
  /// The initial state.
  init('STATE_INIT'),

  /// The state when the heart is busy.
  heartBusy('STATE_HEART_BUSY'),

  /// The state when the heart is being detected.
  heartDetect('STATE_HEART_DETECT'),

  /// The state when there is a wear error.
  heartWearError('STATE_HEART_WEAR_ERROR'),

  /// The normal state of the heart.
  heartNormal('STATE_HEART_NORMAL');

  /// {@macro flutter_veepoo_sdk.heart_statuses}
  const HeartStatuses(this.statusValue);

  /// The value of the state.
  final String statusValue;

  /// Converts a string to a [HeartStatuses].
  factory HeartStatuses.fromString(String status) {
    return HeartStatuses.values.firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
