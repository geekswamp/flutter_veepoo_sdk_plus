package site.shasmatic.flutter_veepoo_sdk

import android.util.Log

/**
 * A utility class for logging messages with a specific tag.
 *
 * This class provides methods to log messages at different levels: debug, info, warning, and error.
 * @see Log
 * @author Ahmad Rifa'i
 */
object VeepooLogger {
    private const val TAG = "FlutterVeepooSDK"

    /**
     * Logs a debug message with the specified tag.
     * @param message The message to be logged.
     * @see Log.d
     */
    fun d(message: String) {
        Log.d(TAG, message)
    }

    /**
     * Logs an informational message with the specified tag.
     * @param message The message to be logged.
     * @see Log.i
     */
    fun i(message: String) {
        Log.i(TAG, message)
    }

    /**
     * Logs a warning message with the specified tag.
     * @param message The message to be logged.
     * @see Log.w
     */
    fun w(message: String) {
        Log.w(TAG, message)
    }

    /**
     * Logs an error message with the specified tag.
     * @param message The message to be logged.
     * @see Log.e
     */
    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}