part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.device_statuses}
/// An enum for device statuses.
/// {@endtemplate}
enum DeviceStatuses {
  /// The status when the device is free.
  free('FREE'),

  /// The status when the device is busy.
  busy('BUSY'),

  /// The status when the device is detecting blood pressure.
  detectBloodPressure('DETECT_BP'),

  /// The status when the device is detecting heart rate.
  detectHeartRate('DETECT_HEART'),

  /// The status when the device is detecting auto five.
  detectAutoFive('DETECT_AUTO_FIVE'),

  /// The status when the device is detecting blood oxygen.
  detectBloodOxygen('DETECT_OXYGEN'),

  /// The status when the device is detecting fatigue.
  detectFatigue('DETECT_FTG'),

  /// The status when the device is detecting pulse rate.
  detectPulseRate('DETECT_PPG'),

  /// The status when the device is charging.
  charging('CHARGING'),

  /// The status when the device is low charging.
  chargeLow('CHARG_LOW'),

  /// The status when the device is not worn.
  unPassWear('UNPASS_WEAR'),

  /// The status when the device is unknown.
  unknown('UNKONW');

  /// {@macro flutter_veepoo_sdk.device_statuses}
  const DeviceStatuses(this.statusValue);

  /// The value of the status.
  final String statusValue;

  /// Converts a string to a [DeviceStatuses].
  static DeviceStatuses fromString(String status) {
    return DeviceStatuses.values.firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
