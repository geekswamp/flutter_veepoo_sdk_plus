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
  final _flutterVeepooSdkPlugin = FlutterVeepooSdk();
  final List<BluetoothResult> _bluetoothResults = [];
  late int _heartRate = 0;
  bool? _deviceConnected = false;

  @override
  void initState() {
    super.initState();
    _requestPermissions();
    _onScanBluetoothResult();
    _onHeartRateResult();
  }

  Future<void> _requestPermissions() async {
    await _flutterVeepooSdkPlugin.requestBluetoothPermissions();
  }

  void _scanDevices() async {
    _flutterVeepooSdkPlugin.scanDevices();
  }

  void _connectDevice(String address) async {
    try {
      await _flutterVeepooSdkPlugin.connectDevice(address);
      await _isDeviceConnected();
    } catch (e) {
      debugPrint('Failed to connect to device: $e');
    }
  }

  void _disconnectDevice() async {
    try {
      await _flutterVeepooSdkPlugin.disconnectDevice();
      setState(() {
        _deviceConnected = false;
      });
    } catch (e) {
      debugPrint('Failed to disconnect from device: $e');
    }
  }

  void _starDetectHeart() async {
    try {
      await _flutterVeepooSdkPlugin.startDetectHeartAfterBinding('0000', true);
    } catch (e) {
      debugPrint('Failed to start detect heart: $e');
    }
  }

  void _stopDetectHeart() async {
    try {
      await _flutterVeepooSdkPlugin.stopDetectHeart();
    } catch (e) {
      debugPrint('Failed to stop detect heart: $e');
    }
  }

  Future<void> _isDeviceConnected() async {
    final bool? isConnected = await _flutterVeepooSdkPlugin.isDeviceConnected();
    setState(() {
      _deviceConnected = isConnected ?? false;
    });
  }

  void _onScanBluetoothResult() {
    _flutterVeepooSdkPlugin.scanBluetoothResult
        .listen((List<BluetoothResult>? results) {
      if (results != null) {
        setState(() {
          _bluetoothResults.clear();
          _bluetoothResults.addAll(results);
        });
      }
    });
  }

  void _onHeartRateResult() {
    _flutterVeepooSdkPlugin.heartRateResult.listen((HeartRateResult? result) {
      if (result != null) {
        setState(() {
          _heartRate = result.data;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: [
            Text(
              'Bluetooth status: $_deviceConnected',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            Text(
              'Heart Rate: $_heartRate',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            ElevatedButton(
              onPressed: _scanDevices,
              child: const Text('Scan Bluetooth Devices'),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _disconnectDevice,
              child: const Text('Disconnect Device'),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _starDetectHeart,
              child: const Text('Start Detect Heart'),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _stopDetectHeart,
              child: const Text('Stop Detect Heart'),
            ),
            const SizedBox(height: 16),
            const Text(
              'Bluetooth Devices:',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            Expanded(
              child: ListView.builder(
                shrinkWrap: true,
                itemCount: _bluetoothResults.length,
                itemBuilder: (_, index) {
                  final result = _bluetoothResults[index];
                  return ListTile(
                    onTap: () => _connectDevice(result.address!),
                    title: Text(result.name ?? 'Unknown'),
                    subtitle: Text(result.address ?? 'Unknown'),
                    trailing: Text('${result.rssi} dBm'),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
