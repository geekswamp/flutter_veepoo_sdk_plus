import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _veepooSdk = VeepooSDK.instance;
  final List<BluetoothDevice> _bluetoothDevices = [];

  void _requestPermissions() async {
    final PermissionStatuses? status =
        await _veepooSdk.requestBluetoothPermissions();
    if (status == PermissionStatuses.permanentlyDenied) {
      await _veepooSdk.openAppSettings();
    }
  }

  void _openBluetooth() async {
    await _veepooSdk.openBluetooth();
  }

  void _closeBluetooth() async {
    await _veepooSdk.closeBluetooth();
  }

  void _scanDevices() async {
    _veepooSdk.scanDevices();
  }

  Future<void> _connectDevice(String address) async {
    try {
      await _veepooSdk.connectDevice(address);
    } catch (e) {
      debugPrint('Failed to connect to device: $e');
    }
  }

  void _disconnectDevice() async {
    try {
      await _veepooSdk.disconnectDevice();
    } catch (e) {
      debugPrint('Failed to disconnect from device: $e');
    }
  }

  void _starDetectHeart() async {
    try {
      await _veepooSdk.startDetectHeartAfterBinding('0000', true);
    } catch (e) {
      debugPrint('Failed to start detect heart: $e');
    }
  }

  void _stopDetectHeart() async {
    try {
      await _veepooSdk.stopDetectHeart();
    } catch (e) {
      debugPrint('Failed to stop detect heart: $e');
    }
  }

  void _startDetectSpoh() async {
    try {
      await _veepooSdk.startDetectSpohAfterBinding('0000', true);
    } catch (e) {
      debugPrint('Failed to start detect spoh: $e');
    }
  }

  void _stopDetectSpoh() async {
    try {
      await _veepooSdk.stopDetectSpoh();
    } catch (e) {
      debugPrint('Failed to stop detect spoh: $e');
    }
  }

  void _readBattery() async {
    try {
      await _veepooSdk.readBattery();
    } catch (e) {
      debugPrint('Failed to read battery: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              StreamBuilder(
                stream: _veepooSdk.heartRate,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    return Text(
                      'Heart Rate: ${snapshot.data?.data ?? 0}',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    );
                  } else {
                    return const Text(
                      'Heart rate no data',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    );
                  }
                },
              ),
              const SizedBox(height: 6),
              StreamBuilder(
                stream: _veepooSdk.spoh,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    return Text(
                      'SPOH: ${snapshot.data?.value ?? 0}',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    );
                  } else {
                    return const Text(
                      'Spoh no data',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    );
                  }
                },
              ),
              const SizedBox(height: 6),
              Wrap(
                spacing: 10,
                runSpacing: 10,
                children: [
                  ElevatedButton(
                    onPressed: _requestPermissions,
                    child: const Text('Request Permissions'),
                  ),
                  ElevatedButton(
                    onPressed: _openBluetooth,
                    child: const Text('Open Bluetooth'),
                  ),
                  ElevatedButton(
                    onPressed: _closeBluetooth,
                    child: const Text('Close Bluetooth'),
                  ),
                  ElevatedButton(
                    onPressed: _scanDevices,
                    child: const Text('Scan Bluetooth Devices'),
                  ),
                  ElevatedButton(
                    onPressed: _disconnectDevice,
                    child: const Text('Disconnect Device'),
                  ),
                  ElevatedButton(
                    onPressed: _starDetectHeart,
                    child: const Text('Start Detect Heart'),
                  ),
                  ElevatedButton(
                    onPressed: _stopDetectHeart,
                    child: const Text('Stop Detect Heart'),
                  ),
                  ElevatedButton(
                    onPressed: _startDetectSpoh,
                    child: const Text('Start Detect SPOH'),
                  ),
                  ElevatedButton(
                    onPressed: _stopDetectSpoh,
                    child: const Text('Stop Detect SPOH'),
                  ),
                  ElevatedButton(
                    onPressed: _readBattery,
                    child: const Text('Read Battery Level'),
                  ),
                ],
              ),
              const Text(
                'Bluetooth Devices:',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              StreamBuilder(
                stream: _veepooSdk.bluetoothDevices,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    _bluetoothDevices.addAll(snapshot.data ?? []);

                    return Expanded(
                      child: ListView.builder(
                        itemCount: _bluetoothDevices.length,
                        itemBuilder: (context, index) {
                          final item = _bluetoothDevices[index];

                          return ListTile(
                            title: Text(item.name ?? 'Unknown'),
                            subtitle: Text(item.address ?? 'Unknown'),
                            trailing: Text('${item.rssi ?? 0} dBm'),
                            onTap: () async {
                              await _connectDevice(item.address ?? '');

                              debugPrint(
                                  'Connected device address: ${await _veepooSdk.getAddress()}');
                            },
                          );
                        },
                      ),
                    );
                  } else {
                    return const Text('No data');
                  }
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
