/// This plugin is a wrapper for the [Veepoo](https://www.veepoo.net/) SDK,
/// it provides a simple way to interact with Veepoo devices.
library flutter_veepoo_sdk;

import 'package:equatable/equatable.dart';
import 'package:flutter/services.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

part 'src/enums/battery_levels.dart';
part 'src/enums/battery_states.dart';
part 'src/enums/device_binding_statuses.dart';
part 'src/enums/device_statuses.dart';
part 'src/enums/heart_statuses.dart';
part 'src/enums/permission_statuses.dart';
part 'src/enums/power_statuses.dart';
part 'src/enums/spoh_statuses.dart';
part 'src/exceptions/battery_exception.dart';
part 'src/exceptions/device_connection_exception.dart';
part 'src/exceptions/heart_detection_exception.dart';
part 'src/exceptions/permission_exception.dart';
part 'src/exceptions/spoh_detection_exception.dart';
part 'src/exceptions/unexpected_event_type_exception.dart';
part 'src/models/battery.dart';
part 'src/models/bluetooth_device.dart';
part 'src/models/heart_rate.dart';
part 'src/models/spoh.dart';
part 'src/flutter_veepoo_sdk_method_channel.dart';
part 'src/flutter_veepoo_sdk_platform_interface.dart';
part 'src/veepoo_sdk.dart';
