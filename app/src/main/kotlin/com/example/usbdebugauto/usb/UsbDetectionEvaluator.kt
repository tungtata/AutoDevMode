package com.tungtata.usbdebugauto.usb

import android.content.Intent
import com.tungtata.usbdebugauto.model.DetectionMode
import com.tungtata.usbdebugauto.model.UsbDetectionResult
import com.tungtata.usbdebugauto.model.UsbState
import com.tungtata.usbdebugauto.repository.LogRepository

class UsbDetectionEvaluator(private val logRepository: LogRepository) {

    /**
     * Parse USB_STATE broadcast intent into UsbState data class
     */
    fun parseUsbStateIntent(intent: Intent): UsbState {
        val connected = intent.getBooleanExtra("connected", false)
        val mtp = intent.getBooleanExtra("mtp", false)
        val ptp = intent.getBooleanExtra("ptp", false)
        val rndis = intent.getBooleanExtra("rndis", false)
        val adb = intent.getBooleanExtra("adb", false)
        val configuration = intent.getIntExtra("configuration", 0)
        val charging = intent.getBooleanExtra("charging", false)
        val charging_dp = intent.getBooleanExtra("charging_dp", false)

        return UsbState(
            connected = connected,
            mtp = mtp,
            ptp = ptp,
            rndis = rndis,
            adb = adb,
            configuration = configuration,
            charging = charging,
            charging_dp = charging_dp
        )
    }

    /**
     * Evaluate USB state based on detection mode
     * This determines if it's a data connection, charge-only, or disconnected
     */
    fun evaluateUsbState(state: UsbState, mode: DetectionMode): UsbDetectionResult {
        // Log the received USB state for debugging
        val stateString = buildString {
            append("USB State: connected=${state.connected}, ")
            append("mtp=${state.mtp}, ptp=${state.ptp}, rndis=${state.rndis}, adb=${state.adb}, ")
            append("configuration=${state.configuration}, charging=${state.charging}")
        }
        logRepository.addLog(stateString)

        // If not connected, it's disconnected
        if (!state.connected) {
            logRepository.addLog("USB disconnected")
            return UsbDetectionResult.Disconnected
        }

        // Evaluate based on detection mode
        val isDataConnection = when (mode) {
            DetectionMode.STRICT -> {
                // Only data if configuration > 0
                state.configuration > 0
            }
            DetectionMode.BALANCED -> {
                // Data if configuration > 0 OR any data function is enabled
                state.configuration > 0 || state.mtp || state.ptp || state.rndis || state.adb
            }
            DetectionMode.LOOSE -> {
                // Any connection is considered data
                true
            }
        }

        val result = if (isDataConnection) {
            logRepository.addLog("Detection result ($mode): DATA CONNECTION")
            UsbDetectionResult.IsDataConnection
        } else {
            logRepository.addLog("Detection result ($mode): CHARGE ONLY")
            UsbDetectionResult.IsChargeOnly
        }

        return result
    }
}
