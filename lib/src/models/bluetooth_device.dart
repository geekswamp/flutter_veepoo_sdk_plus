part of '../../flutter_veepoo_sdk.dart';

/// {@template flutter_veepoo_sdk.bluetooth_device}
/// Represents the result of a Bluetooth scan.
/// {@endtemplate}
class BluetoothDevice extends Equatable {
  /// {@macro flutter_veepoo_sdk.bluetooth_device}
  const BluetoothDevice({this.name, this.address, this.rssi});

  /// The name of the Bluetooth device.
  final String? name;

  /// The MAC address of the Bluetooth device.
  final String? address;

  /// The signal strength of the Bluetooth device.
  final int? rssi;

  /// Converts a [Map<String, dynamic>] to a [BluetoothDevice].
  factory BluetoothDevice.fromJson(Map<String, dynamic> json) {
    return BluetoothDevice(
      name: json['name'],
      address: json['address'],
      rssi: json['rssi'],
    );
  }

  @override
  List<Object?> get props => [name, address, rssi];
}
