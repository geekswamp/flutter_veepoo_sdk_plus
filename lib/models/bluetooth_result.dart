import 'package:equatable/equatable.dart';

/// {@template bluetooth_result}
/// Represents the result of a Bluetooth scan.
/// {@endtemplate}
class BluetoothResult extends Equatable {
  /// {@macro bluetooth_result}
  const BluetoothResult({this.name, this.address, this.rssi});

  /// The name of the Bluetooth device.
  final String? name;

  /// The MAC address of the Bluetooth device.
  final String? address;

  /// The signal strength of the Bluetooth device.
  final int? rssi;

  /// Converts a [Map<String, dynamic>] to a [BluetoothResult].
  factory BluetoothResult.fromJson(Map<String, dynamic> json) {
    return BluetoothResult(
      name: json['name'],
      address: json['address'],
      rssi: json['rssi'],
    );
  }

  @override
  List<Object?> get props => [name, address, rssi];
}
