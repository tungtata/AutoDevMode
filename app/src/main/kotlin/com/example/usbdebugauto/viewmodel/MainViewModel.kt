package com.example.usbdebugauto.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.usbdebugauto.controller.SecureSettingsController
import com.example.usbdebugauto.model.DetectionMode
import com.example.usbdebugauto.model.LogEntry
import com.example.usbdebugauto.model.UsbDetectionResult
import com.example.usbdebugauto.receiver.UsbStateReceiver
import com.example.usbdebugauto.repository.LogRepository
import com.example.usbdebugauto.repository.SettingsRepository
import com.example.usbdebugauto.usb.UsbDetectionEvaluator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val logRepository = LogRepository()
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var settingsController: SecureSettingsController
    private lateinit var usbEvaluator: UsbDetectionEvaluator
    private var usbReceiver: UsbStateReceiver? = null
    private var context: Context? = null

    // UI State flows
    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    // Settings flows (from repository)
    lateinit var automationEnabled: StateFlow<Boolean>
    lateinit var autoEnableDeveloperOptions: StateFlow<Boolean>
    lateinit var autoEnableAdb: StateFlow<Boolean>
    lateinit var autoDisableDeveloperOptions: StateFlow<Boolean>
    lateinit var autoDisableAdb: StateFlow<Boolean>
    lateinit var detectionMode: StateFlow<DetectionMode>
    lateinit var delaySeconds: StateFlow<Int>

    /**
     * Initialize the ViewModel with context
     * Must be called from onCreate of MainActivity
     */
    fun initialize(context: Context) {
        this.context = context
        settingsRepository = SettingsRepository(context)
        settingsController = SecureSettingsController(context, logRepository)
        usbEvaluator = UsbDetectionEvaluator(logRepository)

        // Initialize settings flows - convert from Flow to StateFlow
        automationEnabled = settingsRepository.automationEnabled.stateIn(
            viewModelScope, SharingStarted.Lazily, true
        )
        autoEnableDeveloperOptions = settingsRepository.autoEnableDeveloperOptions.stateIn(
            viewModelScope, SharingStarted.Lazily, true
        )
        autoEnableAdb = settingsRepository.autoEnableAdb.stateIn(
            viewModelScope, SharingStarted.Lazily, true
        )
        autoDisableDeveloperOptions = settingsRepository.autoDisableDeveloperOptions.stateIn(
            viewModelScope, SharingStarted.Lazily, false
        )
        autoDisableAdb = settingsRepository.autoDisableAdb.stateIn(
            viewModelScope, SharingStarted.Lazily, false
        )
        detectionMode = settingsRepository.detectionMode.stateIn(
            viewModelScope, SharingStarted.Lazily, DetectionMode.BALANCED
        )
        delaySeconds = settingsRepository.delaySeconds.stateIn(
            viewModelScope, SharingStarted.Lazily, 0
        )

        // Collect logs
        viewModelScope.launch {
            logRepository.logs.collect { newLogs ->
                _logs.value = newLogs
            }
        }

        // Check permission initially
        checkPermission()

        // Register USB receiver
        registerUsbReceiver(context)

        logRepository.addLog("App initialized successfully")
    }

    private fun registerUsbReceiver(context: Context) {
        if (usbReceiver == null) {
            usbReceiver = UsbStateReceiver()
            val filter = IntentFilter("android.hardware.usb.action.USB_STATE")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(usbReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.registerReceiver(usbReceiver, filter)
            }
            logRepository.addLog("USB State Receiver registered")
        }
    }

    fun checkPermission() {
        _permissionGranted.value = settingsController.isWriteSecureSettingsGranted()
        logRepository.addLog(
            if (_permissionGranted.value) "Permission GRANTED" else "Permission NOT GRANTED"
        )
    }

    fun setAutomationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutomationEnabled(enabled)
            logRepository.addLog("Automation ${if (enabled) "enabled" else "disabled"}")
        }
    }

    fun setAutoEnableDeveloperOptions(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoEnableDeveloperOptions(enabled)
            logRepository.addLog("Auto-enable Developer Options: ${if (enabled) "ON" else "OFF"}")
        }
    }

    fun setAutoEnableAdb(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoEnableAdb(enabled)
            logRepository.addLog("Auto-enable USB Debugging: ${if (enabled) "ON" else "OFF"}")
        }
    }

    fun setAutoDisableDeveloperOptions(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoDisableDeveloperOptions(enabled)
            logRepository.addLog("Auto-disable Developer Options on disconnect: ${if (enabled) "ON" else "OFF"}")
        }
    }

    fun setAutoDisableAdb(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoDisableAdb(enabled)
            logRepository.addLog("Auto-disable USB Debugging on disconnect: ${if (enabled) "ON" else "OFF"}")
        }
    }

    fun setDetectionMode(mode: DetectionMode) {
        viewModelScope.launch {
            settingsRepository.setDetectionMode(mode)
            logRepository.addLog("Detection mode changed to: $mode")
        }
    }

    fun setDelaySeconds(seconds: Int) {
        viewModelScope.launch {
            settingsRepository.setDelaySeconds(seconds)
            logRepository.addLog("Auto-enable delay set to: $seconds seconds")
        }
    }

    // Manual control functions
    fun enableDeveloperOptions() {
        viewModelScope.launch {
            logRepository.addLog("Manual: Enabling Developer Options...")
            settingsController.setDeveloperOptions(true)
        }
    }

    fun disableDeveloperOptions() {
        viewModelScope.launch {
            logRepository.addLog("Manual: Disabling Developer Options...")
            settingsController.setDeveloperOptions(false)
        }
    }

    fun enableAdb() {
        viewModelScope.launch {
            logRepository.addLog("Manual: Enabling USB Debugging...")
            settingsController.setAdbEnabled(true)
        }
    }

    fun disableAdb() {
        viewModelScope.launch {
            logRepository.addLog("Manual: Disabling USB Debugging...")
            settingsController.setAdbEnabled(false)
        }
    }

    fun clearLogs() {
        logRepository.clearLogs()
        _logs.value = emptyList()
        logRepository.addLog("Logs cleared")
    }

    override fun onCleared() {
        super.onCleared()
        if (usbReceiver != null && context != null) {
            try {
                context?.unregisterReceiver(usbReceiver)
                logRepository.addLog("USB State Receiver unregistered")
            } catch (e: Exception) {
                // Receiver might not be registered
            }
        }
    }
}
