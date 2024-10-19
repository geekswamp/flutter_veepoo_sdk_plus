package site.shasmatic.flutter_veepoo_sdk.statuses

/**
 * Enum class representing the different statuses of a permission request.
 */
enum class PermissionStatuses {
    /**
     * Permission is granted.
     */
    GRANTED,

    /**
     * Permission is denied.
     */
    DENIED,

    /**
     * Permission is permanently denied.
     */
    PERMANENTLY_DENIED,

    /**
     * Permission is restricted.
     */
    RESTRICTED,

    /**
     * Permission is unknown.
     */
    UNKNOWN
}