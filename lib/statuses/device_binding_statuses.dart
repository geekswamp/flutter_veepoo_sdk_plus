/// {@template device_binding_statuses}
/// This file contains the device binding statuses.
/// {@endtemplate}
enum DeviceBindingStatus {
  /// Unknown device status.
  unknown('UNKNOWN'),

  /// Failed to check the device.
  checkFail('CHECK_FAIL'),

  /// Successfully checked the device.
  checkSuccess('CHECK_SUCCESS'),

  /// Failed to bind the device.
  settingFail('SETTING_FAIL'),

  /// Successfully bound the device.
  settingSuccess('SETTING_SUCCESS'),

  /// Failed to read the device.
  readFail('READ_FAIL'),

  /// Successfully read the device.
  readSuccess('READ_SUCCESS'),

  /// Successfully check time.
  checkAndTimeSuccess('CHECK_AND_TIME_SUCCESS'),
  ;

  /// {@macro device_binding_statuses}
  const DeviceBindingStatus(this.statusValue);

  /// The value of the status.
  final String statusValue;

  /// Converts a string to a [DeviceBindingStatus].
  static DeviceBindingStatus fromString(String status) {
    return DeviceBindingStatus.values
        .firstWhere((e) => e.statusValue == status);
  }

  @override
  toString() {
    return statusValue;
  }
}
