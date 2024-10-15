package site.shasmatic.flutter_veepoo_sdk.entities

/**
 * Data class representing a Bluetooth scan result.
 *
 * This class provides properties to store the name, address, and RSSI of a Bluetooth device.
 *
 * @constructor Creates a new [BluetoothResult] instance with the specified name, address, and RSSI.
 * @param name The name of the Bluetooth device.
 * @param address The address of the Bluetooth device.
 * @param rssi The RSSI value of the Bluetooth device.
 */
class BluetoothResult(val name: String?, val address: String?, val rssi: Int?)