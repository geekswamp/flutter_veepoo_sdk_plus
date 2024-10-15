import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_veepoo_sdk_platform_interface.dart';

/// An implementation of [FlutterVeepooSdkPlatform] that uses method channels.
class MethodChannelFlutterVeepooSdk extends FlutterVeepooSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_veepoo_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
