/// {@template battery_states}
/// This file contains the enum for battery states
/// {@endtemplate}
enum BatteryStates {
  /// The battery is wake up.
  wakeUp(0),

  /// The battery is sleep.
  sleep(1);

  /// {@macro battery_states}
  const BatteryStates(this.intValue);

  /// The value of the battery state.
  final int intValue;

  /// Converts an integer to a [BatteryStates].
  static BatteryStates fromInt(int value) {
    return BatteryStates.values.firstWhere((e) => e.intValue == value);
  }

  @override
  toString() {
    return '$intValue';
  }
}