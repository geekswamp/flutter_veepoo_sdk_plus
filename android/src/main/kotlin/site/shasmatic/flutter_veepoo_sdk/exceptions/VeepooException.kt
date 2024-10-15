package site.shasmatic.flutter_veepoo_sdk.exceptions

/**
 * Base class for all exceptions thrown by the Veepoo SDK.
 *
 * @constructor Creates a new [VeepooException] with the given error message and cause.
 * @param message The error message.
 * @param cause The cause of the exception.
 * @see Exception
 * @see Throwable
 * @author Ahmad Rifa'i
 */
abstract class VeepooException(message: String, cause: Throwable? = null) : Exception(message, cause)