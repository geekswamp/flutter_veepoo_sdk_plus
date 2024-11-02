part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.heart_rate}
/// A class that represents the result of a heart rate detection.
/// {@endtemplate}
final class HeartRate extends Equatable {
  /// {@macro flutter_veepoo_sdk.heart_rate}
  const HeartRate(this.data, this.state);

  /// The heart rate data.
  final int data;

  /// The state of the heart.
  final HeartStatuses? state;

  /// Converts a [Map<String, dynamic>] to a [HeartRate].
  factory HeartRate.fromJson(Map<String, dynamic> json) {
    return HeartRate(json['data'], HeartStatuses.fromString(json['state']));
  }

  @override
  List<Object?> get props => [data, state];
}
