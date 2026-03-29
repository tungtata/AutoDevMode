package com.example.usbdebugauto.model

/**
 * Detection mode for determining USB data vs charge-only
 */
enum class DetectionMode {
    /**
     * STRICT: Only consider USB data if connected=true AND configuration > 0
     */
    STRICT,

    /**
     * BALANCED: Consider USB data if connected=true AND (mtp OR ptp OR rndis OR adb OR configuration > 0)
     * This is the default and recommended mode
     */
    BALANCED,

    /**
     * LOOSE: Only require connected=true (may have false positives)
     */
    LOOSE
}

/**
 * Result of USB detection evaluation
 */
sealed class UsbDetectionResult {
    data object IsChargeOnly : UsbDetectionResult()
    data object IsDataConnection : UsbDetectionResult()
    data object Disconnected : UsbDetectionResult()
}

/**
 * USB State data class representing the current USB connection state
 */
data class UsbState(
    val connected: Boolean = false,
    val mtp: Boolean = false,
    val ptp: Boolean = false,
    val rndis: Boolean = false,
    val adb: Boolean = false,
    val configuration: Int = 0,
    val charging: Boolean = false,
    val charging_dp: Boolean = false
)

/**
 * Result of setting operations
 */
sealed class SettingOperationResult {
    data object Success : SettingOperationResult()
    data class Error(val exception: Throwable) : SettingOperationResult()
}

/**
 * Log entry for activity tracking
 */
data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: String = "INFO",
    val message: String = ""
) {
    fun toDisplayString(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.US)
        val time = sdf.format(java.util.Date(timestamp))
        return "[$time] $level: $message"
    }
}
