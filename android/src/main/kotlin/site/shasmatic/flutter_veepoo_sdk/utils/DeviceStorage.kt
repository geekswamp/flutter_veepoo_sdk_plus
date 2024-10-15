package site.shasmatic.flutter_veepoo_sdk.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * A utility class for handling device storage using [SharedPreferences].
 *
 * This class provides methods to save and retrieve user credentials, such as password
 * and interface settings (24-hour format), in the device's [SharedPreferences].
 *
 * @constructor Initializes the [DeviceStorage] with the given context.
 * @param context The application context used to access [SharedPreferences].
 */
class DeviceStorage(private val context: Context) {

    companion object {
        private const val PREF_NAME = "device_storage"
        private const val KEY_ADDRESS = "address"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_24H = "is24H"
    }

    /**
     * Retrieves the [SharedPreferences] instance.
     */
    private fun getSharedPreferences(): SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Retrieves the [SharedPreferences.Editor] instance.
     */
    private val editor = getSharedPreferences().edit()

    /**
     * Retrieves the saved address from [SharedPreferences].
     *
     * @return The saved address if it exists, or null if not.
     */
    fun getAddress(): String? = getSharedPreferences().getString(KEY_ADDRESS, null)

    /**
     * Retrieves the saved password from [SharedPreferences].
     *
     * @return The saved password if it exists, or null if not.
     */
    fun getPassword(): String? = getSharedPreferences().getString(KEY_PASSWORD, null)

    /**
     * Retrieves the user's preferred time format from [SharedPreferences].
     *
     * @return `true` if the 24-hour format is enabled; `false` otherwise.
     */
    fun get24H(): Boolean = getSharedPreferences().getBoolean(KEY_IS_24H, true)

    /**
     * Saves the given address into the [SharedPreferences].
     *
     * @param address The address to be saved.
     */
    fun saveAddress(address: String?) {
        editor.putString(KEY_ADDRESS, address)
        editor.apply()
    }

    /**
     * Saves the user credentials, including the address, password, and the 24-hour format preference, into the [SharedPreferences].
     *
     * @param address The address to be saved.
     * @param password The password to be saved.
     * @param is24H A flag indicating whether the 24-hour format is preferred.
     */
    fun saveCredentials(password: String, is24H: Boolean) {
        editor.putString(KEY_PASSWORD, password)
        editor.putBoolean(KEY_IS_24H, is24H)
        editor.apply()
    }
}