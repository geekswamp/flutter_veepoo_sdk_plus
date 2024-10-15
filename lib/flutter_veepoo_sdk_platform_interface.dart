import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_veepoo_sdk_method_channel.dart';

abstract class FlutterVeepooSdkPlatform extends PlatformInterface {
  /// Constructs a FlutterVeepooSdkPlatform.
  FlutterVeepooSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterVeepooSdkPlatform _instance = MethodChannelFlutterVeepooSdk();

  /// The default instance of [FlutterVeepooSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterVeepooSdk].
  static FlutterVeepooSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterVeepooSdkPlatform] when
  /// they register themselves.
  static set instance(FlutterVeepooSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
