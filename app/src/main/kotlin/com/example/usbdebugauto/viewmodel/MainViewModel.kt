package com.tungtata.usbdebugauto.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tungtata.usbdebugauto.controller.SecureSettingsController
import com.tungtata.usbdebugauto.model.DetectionMode
import com.tungtata.usbdebugauto.model.LogEntry
import com.tungtata.usbdebugauto.model.UsbDetectionResult
import com.tungtata.usbdebugauto.receiver.UsbStateReceiver
import com.tungtata.usbdebugauto.repository.LogRepository
import com.tungtata.usbdebugauto.repository.SettingsRepository
import com.tungtata.usbdebugauto.usb.UsbDetectionEvaluator
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

    // Settings flows (from repository) - Initialize with default values
    private val _automationEnabled = MutableStateFlow(true)
    val automationEnabled: StateFlow<Boolean> = _automationEnabled.asStateFlow()

    private val _autoEnableDeveloperOptions = MutableStateFlow(true)
    val autoEnableDeveloperOptions: StateFlow<Boolean> = _autoEnableDeveloperOptions.asStateFlow()

    private val _autoEnableAdb = MutableStateFlow(true)
    val autoEnableAdb: StateFlow<Boolean> = _autoEnableAdb.asStateFlow()

    private val _autoDisableDeveloperOptions = MutableStateFlow(false)
    val autoDisableDeveloperOptions: StateFlow<Boolean> = _autoDisableDeveloperOptions.asStateFlow()

    private val _autoDisableAdb = MutableStateFlow(false)
    val autoDisableAdb: StateFlow<Boolean> = _autoDisableAdb.asStateFlow()

    private val _detectionMode = MutableStateFlow(DetectionMode.LOOSE)
    val detectionMode: StateFlow<DetectionMode> = _detectionMode.asStateFlow()

    private val _delaySeconds = MutableStateFlow(0)
    val delaySeconds: StateFlow<Int> = _delaySeconds.asStateFlow()

    // Current status flows
    private val _developerOptionsEnabled = MutableStateFlow<Boolean?>(null)
    val developerOptionsEnabled: StateFlow<Boolean?> = _developerOptionsEnabled.asStateFlow()

    private val _adbEnabled = MutableStateFlow<Boolean?>(null)
    val adbEnabled: StateFlow<Boolean?> = _adbEnabled.asStateFlow()

    // Toast notification option
    private val _showStatusToast = MutableStateFlow(false)
    val showStatusToast: StateFlow<Boolean> = _showStatusToast.asStateFlow()

    /**
     * Initialize the ViewModel with context
     * Must be called from onCreate of MainActivity
     */
    fun initialize(context: Context) {
        this.context = context
        settingsRepository = SettingsRepository(context)
        settingsController = SecureSettingsController(context, logRepository)
        usbEvaluator = UsbDetectionEvaluator(logRepository)

        // Initialize settings flows - sync repository values to local StateFlows
        viewModelScope.launch {
            settingsRepository.automationEnabled.collect { value ->
                _automationEnabled.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.autoEnableDeveloperOptions.collect { value ->
                _autoEnableDeveloperOptions.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.autoEnableAdb.collect { value ->
                _autoEnableAdb.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.autoDisableDeveloperOptions.collect { value ->
                _autoDisableDeveloperOptions.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.autoDisableAdb.collect { value ->
                _autoDisableAdb.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.detectionMode.collect { value ->
                _detectionMode.value = value
            }
        }
        viewModelScope.launch {
            settingsRepository.delaySeconds.collect { value ->
                _delaySeconds.value = value
            }
        }

        // Collect logs
        viewModelScope.launch {
            logRepository.logs.collect { newLogs ->
                _logs.value = newLogs
            }
        }

        // Check permission initially
        checkPermission()

        // Refresh status initially
        refreshStatus()

        // Start periodic status refresh (every 2 seconds)
        viewModelScope.launch {
            while (true) {
                delay(2000)
                refreshStatus()
            }
        }

        // Register USB receiver
        registerUsbReceiver(context)

        logRepository.addLog("App initialized successfully")
    }

    private fun refreshStatus() {
        val devOpt = settingsController.getDeveloperOptionsState()
        val adb = settingsController.getAdbState()
        
        if (devOpt != _developerOptionsEnabled.value) {
            _developerOptionsEnabled.value = devOpt
            if (devOpt != null) {
                logRepository.addLog("Developer Options: ${if (devOpt) "ENABLED" else "DISABLED"}")
            }
        }
        
        if (adb != _adbEnabled.value) {
            _adbEnabled.value = adb
            if (adb != null) {
                logRepository.addLog("USB Debugging: ${if (adb) "ENABLED" else "DISABLED"}")
            }
        }
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

    fun setShowStatusToast(show: Boolean) {
        _showStatusToast.value = show
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
