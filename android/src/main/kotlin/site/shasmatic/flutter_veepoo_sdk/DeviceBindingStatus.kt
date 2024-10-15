package site.shasmatic.flutter_veepoo_sdk

/**
 * Enum class representing the status of a device binding operation.
 *
 * @constructor Creates a new [DeviceBindingStatus] instance with the specified status.
 */
enum class DeviceBindingStatus {

    /**
     * Unknown status.
     */
    UNKNOWN,

    /**
     * Failed to check the device.
     */
    CHECK_FAIL,

    /**
     * Successfully checked the device.
     */
    CHECK_SUCCESS,

    /**
     * Failed to bind the device.
     */
    SETTING_FAIL,

    /**
     * Successfully bound the device.
     */
    SETTING_SUCCESS,

    /**
     * Failed to read the device.
     */
    READ_FAIL,

    /**
     * Successfully read the device.
     */
    READ_SUCCESS,

    /**
     * Successfully checked and timed the device.
     */
    CHECK_AND_TIME_SUCCESS
}