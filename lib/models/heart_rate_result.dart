import 'package:equatable/equatable.dart';
import 'package:flutter_veepoo_sdk/statuses/heart_statuses.dart';

/// {@template heart_rate_result}
/// A class that represents the result of a heart rate detection.
/// {@endtemplate}
class HeartRateResult extends Equatable {
  /// {@macro heart_rate_result}
  const HeartRateResult(this.data, this.state);

  /// The heart rate data.
  final int data;

  /// The state of the heart.
  final HeartStatuses? state;

  /// Converts a [Map<String, dynamic>] to a [HeartRateResult].
  factory HeartRateResult.fromJson(Map<String, dynamic> map) {
    return HeartRateResult(map['data'], HeartStatuses.fromString(map['state']));
  }

  @override
  List<Object?> get props => [data, state];
}
