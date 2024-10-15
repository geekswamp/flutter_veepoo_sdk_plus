
import 'flutter_veepoo_sdk_platform_interface.dart';

class FlutterVeepooSdk {
  Future<String?> getPlatformVersion() {
    return FlutterVeepooSdkPlatform.instance.getPlatformVersion();
  }
}
