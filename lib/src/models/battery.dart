part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.battery}
/// Represents the result of a battery.
/// {@endtemplate}
class Battery extends Equatable {
  /// {@macro flutter_veepoo_sdk.battery}
  const Battery(
    this.level,
    this.percent,
    this.powerModel,
    this.state,
    this.bat,
    this.isLow,
    this.isPercent,
  );

  /// The level of the battery.
  final BatteryLevels? level;

  /// The percent of the battery.
  final int? percent;

  /// The power model of the battery.
  final PowerStatuses? powerModel;

  /// The state of the battery.
  final BatteryStates? state;

  /// Current battery level of BAT.
  final int? bat;

  /// Whether the battery is low.
  final bool? isLow;

  /// Whether the battery is percent.
  final bool? isPercent;

  /// Converts a [Map<String, dynamic>] to a [Battery].
  factory Battery.fromJson(Map<String, dynamic> json) {
    return Battery(
      BatteryLevels.fromInt(json['level']),
      json['percent'],
      PowerStatuses.fromInt(json['powerModel']),
      BatteryStates.fromInt(json['state']),
      json['bat'],
      json['isLow'],
      json['isPercent'],
    );
  }

  @override
  List<Object?> get props => [
        level,
        percent,
        powerModel,
        state,
        bat,
        isLow,
        isPercent,
      ];
}
