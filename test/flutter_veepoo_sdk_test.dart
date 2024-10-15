// import 'package:flutter_test/flutter_test.dart';
// import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk.dart';
// import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk_platform_interface.dart';
// import 'package:flutter_veepoo_sdk/flutter_veepoo_sdk_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';
//
// class MockFlutterVeepooSdkPlatform
//     with MockPlatformInterfaceMixin
//     implements FlutterVeepooSdkPlatform {
//
//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }
//
// void main() {
//   final FlutterVeepooSdkPlatform initialPlatform = FlutterVeepooSdkPlatform.instance;
//
//   test('$MethodChannelFlutterVeepooSdk is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelFlutterVeepooSdk>());
//   });
//
//   test('getPlatformVersion', () async {
//     FlutterVeepooSdk flutterVeepooSdkPlugin = FlutterVeepooSdk();
//     MockFlutterVeepooSdkPlatform fakePlatform = MockFlutterVeepooSdkPlatform();
//     FlutterVeepooSdkPlatform.instance = fakePlatform;
//
//     expect(await flutterVeepooSdkPlugin.getPlatformVersion(), '42');
//   });
// }
