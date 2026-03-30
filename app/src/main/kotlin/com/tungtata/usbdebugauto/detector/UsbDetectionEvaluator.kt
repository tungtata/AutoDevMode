package com.tungtata.usbdebugauto.detector

import android.content.Intent
import com.tungtata.usbdebugauto.DetectionMode

class UsbDetectionEvaluator {
    fun isUsbDataConnected(intent: Intent, mode: DetectionMode = DetectionMode.LOOSE): Boolean {
        return when (mode) {
            DetectionMode.STRICT -> isStrictMode(intent)
            DetectionMode.BALANCED -> isBalancedMode(intent)
            DetectionMode.LOOSE -> isLooseMode(intent)
        }
    }

    private fun isStrictMode(intent: Intent): Boolean {
        val connected = intent.getBooleanExtra("connected", false)
        val hasMtp = intent.getBooleanExtra("mtp", false)
        val hasPtp = intent.getBooleanExtra("ptp", false)
        return connected && (hasMtp || hasPtp)
    }

    private fun isBalancedMode(intent: Intent): Boolean {
        val connected = intent.getBooleanExtra("connected", false)
        val hasRndis = intent.getBooleanExtra("rndis", false)
        val hasMtp = intent.getBooleanExtra("mtp", false)
        val hasPtp = intent.getBooleanExtra("ptp", false)
        return connected && (hasMtp || hasPtp || hasRndis)
    }

    private fun isLooseMode(intent: Intent): Boolean {
        val connected = intent.getBooleanExtra("connected", false)
        return connected
    }
}
