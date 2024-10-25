part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.power_statuses}
/// This file contains the enum for power statuses
/// {@endtemplate}
enum PowerStatuses {
  /// The power is normal.
  normal(0),

  /// The power is charging.
  charging(1),

  /// The power is low voltage.
  lowVoltage(2),

  /// The power is full.
  full(3);

  /// {@macro flutter_veepoo_sdk.power_statuses}
  const PowerStatuses(this.intValue);

  /// The value of the power status.
  final int intValue;

  /// Converts an integer to a [PowerStatuses].
  static PowerStatuses fromInt(int value) {
    return PowerStatuses.values.firstWhere((e) => e.intValue == value);
  }

  @override
  toString() {
    return '$intValue';
  }
}
