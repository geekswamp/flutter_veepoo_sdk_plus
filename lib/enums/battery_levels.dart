/// {@template battery_levels}
/// This file contains the enum for battery levels
/// {@endtemplate}
enum BatteryLevels {
  /// Low battery level.
  low(1),

  /// Medium battery level.
  medium(2),

  /// High battery level.
  high(3),

  /// Full battery level.
  full(4);

  /// {@macro battery_levels}
  const BatteryLevels(this.intValue);

  /// The value of the battery level.
  final int intValue;

  /// Converts an integer to a [BatteryLevels].
  static BatteryLevels fromInt(int value) {
    return BatteryLevels.values.firstWhere((e) => e.intValue == value);
  }

  @override
  toString() {
    return '$intValue';
  }
}