package site.shasmatic.flutter_veepoo_sdk.exceptions

/**
 * Base class for all exceptions thrown by the Veepoo SDK.
 *
 * @constructor Creates a new [VPException] with the given error message and cause.
 * @param message The error message.
 * @param cause The cause of the exception.
 * @see Exception
 * @see Throwable
 */
class VPException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /**
     * Returns a string representation of the exception.
     *
     * @return A string representation of the exception.
     */
    override fun toString(): String {
        return "VPException: $message, ${cause?.message}"
    }
}