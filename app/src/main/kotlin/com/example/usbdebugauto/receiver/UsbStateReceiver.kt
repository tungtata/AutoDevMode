package com.tungtata.usbdebugauto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tungtata.usbdebugauto.controller.SecureSettingsController
import com.tungtata.usbdebugauto.model.SettingOperationResult
import com.tungtata.usbdebugauto.model.UsbDetectionResult
import com.tungtata.usbdebugauto.repository.LogRepository
import com.tungtata.usbdebugauto.repository.SettingsRepository
import com.tungtata.usbdebugauto.usb.UsbDetectionEvaluator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver for monitoring USB connection state changes
 *
 * This receiver listens to android.hardware.usb.action.USB_STATE broadcasts
 * which are sent whenever USB connection state changes.
 *
 * Note: This receiver is declared in AndroidManifest.xml as exported=true
 * so it can receive system broadcasts. The receiver is active even when the app
 * is not running (if automation is enabled and USB disconnects).
 */
class UsbStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        // Must be USB_STATE action
        if (intent.action != "android.hardware.usb.action.USB_STATE") return

        // Initialize repositories and controllers
        val logRepository = LogRepository()
        val settingsRepository = SettingsRepository(context)
        val settingsController = SecureSettingsController(context, logRepository)
        val evaluator = UsbDetectionEvaluator(logRepository)

        // Parse USB state from intent
        val usbState = evaluator.parseUsbStateIntent(intent)

        // Run async to read from DataStore and perform actions
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Read current settings
                val automationEnabled = settingsRepository.automationEnabled.first()
                if (!automationEnabled) {
                    logRepository.addLog("Automation is disabled, ignoring USB event")
                    return@launch
                }

                val detectionMode = settingsRepository.detectionMode.first()
                val delaySeconds = settingsRepository.delaySeconds.first()

                // Evaluate USB state
                val detectionResult = evaluator.evaluateUsbState(usbState, detectionMode)

                when (detectionResult) {
                    is UsbDetectionResult.IsDataConnection -> {
                        logRepository.addLog("USB Data Connection detected")

                        // Wait for configured delay
                        if (delaySeconds > 0) {
                            logRepository.addLog("Waiting $delaySeconds seconds before auto-enabling...")
                            delay(delaySeconds * 1000L)
                        }

                        // Check and enable settings based on user preferences
                        val autoEnableDev = settingsRepository.autoEnableDeveloperOptions.first()
                        val autoEnableAdb = settingsRepository.autoEnableAdb.first()

                        if (autoEnableDev) {
                            settingsController.setDeveloperOptions(true)
                        }

                        if (autoEnableAdb) {
                            settingsController.setAdbEnabled(true)
                        }
                    }

                    is UsbDetectionResult.IsChargeOnly -> {
                        logRepository.addLog("USB Charge-Only detected, no action taken")
                    }

                    is UsbDetectionResult.Disconnected -> {
                        logRepository.addLog("USB disconnected")

                        // Check and disable settings if auto-disable is enabled
                        val autoDisableDev = settingsRepository.autoDisableDeveloperOptions.first()
                        val autoDisableAdb = settingsRepository.autoDisableAdb.first()

                        if (autoDisableDev) {
                            logRepository.addLog("Auto-disabling Developer Options...")
                            settingsController.setDeveloperOptions(false)
                        }

                        if (autoDisableAdb) {
                            logRepository.addLog("Auto-disabling USB Debugging...")
                            settingsController.setAdbEnabled(false)
                        }
                    }
                }

            } catch (e: Exception) {
                logRepository.addError("Error in UsbStateReceiver: ${e.message}")
            }
        }
    }
}
