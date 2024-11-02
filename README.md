# flutter_veepoo_sdk

Flutter plugin for Veepoo smartwatch SDK. This plugin currently supports only on Android.

> Note: This plugin is still in development and not ready for production use.

## Platform Support

| **Features**                               | **Android** | **iOS**           |
|--------------------------------------------|-------------|-------------------|
| Request Bluetooth Permissions              | Supported   | Not Supported Yet |
| Scan Bluetooth Devices                     | Supported   | Not Supported Yet |
| Stop Scan Bluetooth Devices                | Supported   | Not Supported Yet |
| Connect to Bluetooth Device                | Supported   | Not Supported Yet |
| Disconnect from Bluetooth Device           | Supported   | Not Supported Yet |
| Get Connected Bluetooth Device Mac Address | Supported   | Not Supported Yet |
| Get Current Connection Status              | Supported   | Not Supported Yet |
| Check Device Connection                    | Supported   | Not Supported Yet |
| Open App Settings                          | Supported   | Not Supported Yet |
| Open Bluetooth                             | Supported   | Not Supported Yet |
| Close Bluetooth                            | Supported   | Not Supported Yet |
| Bind Device with PIN                       | Supported   | Not Supported Yet |
| Start Detect Heart Rate                    | Supported   | Not Supported Yet |
| Stop Detect Heart Rate                     | Supported   | Not Supported Yet |
| Start Detect SpO2                          | Supported   | Not Supported Yet |
| Stop Detect SpO2                           | Supported   | Not Supported Yet |
| Setting Heart Rate Alarm                   | Supported   | Not Supported Yet |
| Read Setting Heart Rate Alarm              | Supported   | Not Supported Yet |
| Get Device Battery Level                   | Supported   | Not Supported Yet |

## How to use

1. Add the plugin to your `pubspec.yaml` file:

```yaml
dependencies:
  flutter_veepoo_sdk: ^0.0.4
```
or add the plugin via git:

```yaml
dependencies:
  flutter_veepoo_sdk:
    git:
      url: https://github.com/geekswamp/flutter_veepoo_sdk_plus.git
      ref: main
```

2. Add Bluetooth, access network state, and location permissions to your `AndroidManifest.xml` file:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
    <uses-permission android:name="android.permission.BLUETOOTH_SCAN"
                     android:usesPermissionFlags="neverForLocation"
                     tools:targetApi="s"/>
    
    <uses-feature
                android:name="android.hardware.bluetooth_le"
                android:required="true"/>
    
    <!-- Your other manifest configurations -->
</manifest>
```

3. Import the plugin in your Dart code:

```dart
import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';
```

4. Initialize the plugin:

```dart
VeepooSDK _veepooSDK = VeepooSDK.instance;
```

5. Use the plugin: 

- First, you need to request Bluetooth and location permissions.

```dart
await _veepooSDK.requestBluetoothPermissions();
```

- Then, you can start scanning for available devices:

```dart
await _veepooSDK.scanDevices();
```
- You can listen to the scan results using the `scanBluetoothResult` stream:

```dart
_veepooSDK.bluetoothDevices.listen((List<BluetoothDevice>? devices) {
  print(results);
});
```
- You can connect to a device using the `connectDevice` method:

```dart
await _veepooSDK.connectDevice(deviceAddress);
```

- Also, you can disconnect from a device using the `disconnectDevice` method:

```dart
await _veepooSDK.disconnectDevice();
```

- You can stop scanning for devices using the `stopScanDevices` method:

```dart
await _veepooSDK.stopScanDevices();
```