part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.spoh}
/// Represents the result of a SPOH (blood oxygen).
/// {@endtemplate}
final class Spoh extends Equatable {
  /// {@macro flutter_veepoo_sdk.spoh}
  const Spoh(
    this.spohStatuses,
    this.deviceStatuses,
    this.value,
    this.checking,
    this.checkingProgress,
    this.rate,
  );

  /// The status of the SPOH.
  final SpohStatuses? spohStatuses;

  /// The status of the device.
  final DeviceStatuses? deviceStatuses;

  /// The value of the SPOH.
  final int? value;

  /// The checking status of the SPOH.
  final bool? checking;

  /// The checking progress of the SPOH.
  final int? checkingProgress;

  /// The rate of the SPOH.
  final int? rate;

  /// Converts a [Map<String, dynamic>] to a [Spoh].
  factory Spoh.fromJson(Map<String, dynamic> json) {
    return Spoh(
      SpohStatuses.fromString(json['spohStatus']),
      DeviceStatuses.fromString(json['deviceStatus']),
      json['value'],
      json['checking'],
      json['checkingProgress'],
      json['rate'],
    );
  }

  @override
  List<Object?> get props => [
        spohStatuses,
        deviceStatuses,
        value,
        checking,
        checkingProgress,
        rate,
      ];
}
