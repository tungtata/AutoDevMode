package com.tungtata.usbdebugauto.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import com.tungtata.usbdebugauto.controller.SecureSettingsController
import com.tungtata.usbdebugauto.detector.UsbDetectionEvaluator
import com.tungtata.usbdebugauto.repository.SettingsRepository
import com.tungtata.usbdebugauto.repository.LogRepository
import kotlinx.coroutines.delay

class UsbStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action != "android.hardware.usb.action.USB_STATE") return

        val detectionEvaluator = UsbDetectionEvaluator()
        val isConnected = detectionEvaluator.isUsbDataConnected(intent)

        CoroutineScope(Dispatchers.IO).launch {
            val settingsRepository = SettingsRepository(context)
            val automationEnabled = settingsRepository.automationEnabled.first()

            if (!automationEnabled) return@launch

            val settingsController = SecureSettingsController(context)
            val logRepository = LogRepository()

            if (isConnected) {
                val autoEnableDev = settingsRepository.autoEnableDeveloperOptions.first()
                val autoEnableAdb = settingsRepository.autoEnableAdb.first()
                val delaySeconds = settingsRepository.delaySeconds.first()

                if (autoEnableDev) {
                    settingsController.enableDeveloperOptions()
                    logRepository.addLog("Auto-enabled Developer Options")
                }

                if (autoEnableAdb) {
                    delay(delaySeconds * 1000L)
                    settingsController.enableAdb()
                    logRepository.addLog("Auto-enabled USB Debugging")
                }
            } else {
                val autoDisableDev = settingsRepository.autoDisableDeveloperOptions.first()
                val autoDisableAdb = settingsRepository.autoDisableAdb.first()

                if (autoDisableDev) {
                    settingsController.disableDeveloperOptions()
                    logRepository.addLog("Auto-disabled Developer Options")
                }

                if (autoDisableAdb) {
                    settingsController.disableAdb()
                    logRepository.addLog("Auto-disabled USB Debugging")
                }
            }
        }
    }
}
