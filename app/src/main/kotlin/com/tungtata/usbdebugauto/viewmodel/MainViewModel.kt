package com.tungtata.usbdebugauto.viewmodel

import android.content.Context
import android.content.IntentFilter
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tungtata.usbdebugauto.DetectionMode
import com.tungtata.usbdebugauto.LogEntry
import com.tungtata.usbdebugauto.controller.SecureSettingsController
import com.tungtata.usbdebugauto.receiver.UsbStateReceiver
import com.tungtata.usbdebugauto.repository.LogRepository
import com.tungtata.usbdebugauto.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var logRepository: LogRepository
    private lateinit var settingsController: SecureSettingsController
    private var usbStateReceiver: UsbStateReceiver? = null
    private var context: Context? = null

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    private val _automationEnabled = MutableStateFlow(false)
    val automationEnabled: StateFlow<Boolean> = _automationEnabled.asStateFlow()

    private val _autoEnableDeveloperOptions = MutableStateFlow(true)
    val autoEnableDeveloperOptions: StateFlow<Boolean> = _autoEnableDeveloperOptions.asStateFlow()

    private val _autoEnableAdb = MutableStateFlow(true)
    val autoEnableAdb: StateFlow<Boolean> = _autoEnableAdb.asStateFlow()

    private val _autoDisableDeveloperOptions = MutableStateFlow(false)
    val autoDisableDeveloperOptions: StateFlow<Boolean> = _autoDisableDeveloperOptions.asStateFlow()

    private val _autoDisableAdb = MutableStateFlow(false)
    val autoDisableAdb: StateFlow<Boolean> = _autoDisableAdb.asStateFlow()

    private val _delaySeconds = MutableStateFlow(0)
    val delaySeconds: StateFlow<Int> = _delaySeconds.asStateFlow()

    private val _detectionMode = MutableStateFlow(DetectionMode.LOOSE)
    val detectionMode: StateFlow<DetectionMode> = _detectionMode.asStateFlow()

    private val _developerOptionsEnabled = MutableStateFlow<Boolean?>(null)
    val developerOptionsEnabled: StateFlow<Boolean?> = _developerOptionsEnabled.asStateFlow()

    private val _adbEnabled = MutableStateFlow<Boolean?>(null)
    val adbEnabled: StateFlow<Boolean?> = _adbEnabled.asStateFlow()

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    private val _showStatusToast = MutableStateFlow(false)
    val showStatusToast: StateFlow<Boolean> = _showStatusToast.asStateFlow()

    fun initialize(context: Context) {
        this.context = context
        settingsRepository = SettingsRepository(context)
        logRepository = LogRepository()
        settingsController = SecureSettingsController(context)

        // Register USB state receiver
        usbStateReceiver = UsbStateReceiver()
        val filter = IntentFilter("android.hardware.usb.action.USB_STATE")
        context.registerReceiver(usbStateReceiver, filter, Context.RECEIVER_EXPORTED)

        viewModelScope.launch {
            settingsRepository.permissionGranted.collect { _permissionGranted.value = it }
        }

        viewModelScope.launch {
            settingsRepository.automationEnabled.collect { _automationEnabled.value = it }
        }

        viewModelScope.launch {
            settingsRepository.autoEnableDeveloperOptions.collect { _autoEnableDeveloperOptions.value = it }
        }

        viewModelScope.launch {
            settingsRepository.autoEnableAdb.collect { _autoEnableAdb.value = it }
        }

        viewModelScope.launch {
            settingsRepository.autoDisableDeveloperOptions.collect { _autoDisableDeveloperOptions.value = it }
        }

        viewModelScope.launch {
            settingsRepository.autoDisableAdb.collect { _autoDisableAdb.value = it }
        }

        viewModelScope.launch {
            settingsRepository.delaySeconds.collect { _delaySeconds.value = it }
        }

        viewModelScope.launch {
            settingsRepository.showStatusToast.collect { _showStatusToast.value = it }
        }

        viewModelScope.launch {
            while (true) {
                refreshStatus()
                delay(2000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (usbStateReceiver != null && context != null) {
            try {
                context!!.unregisterReceiver(usbStateReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkPermission() {
        viewModelScope.launch {
            val hasPermission = checkWriteSecureSettingsPermission()
            _permissionGranted.value = hasPermission
            settingsRepository.setPermissionGranted(hasPermission)
        }
    }

    private fun checkWriteSecureSettingsPermission(): Boolean {
        if (context == null) return false
        return try {
            val currentValue = Settings.Secure.getInt(context!!.contentResolver, "development_settings_enabled", 0)
            Settings.Secure.putInt(context!!.contentResolver, "development_settings_enabled", currentValue)
            true
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun setAutomationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutomationEnabled(enabled)
        }
    }

    fun setAutoEnableDeveloperOptions(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoEnableDeveloperOptions(enabled)
        }
    }

    fun setAutoEnableAdb(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoEnableAdb(enabled)
        }
    }

    fun setAutoDisableDeveloperOptions(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoDisableDeveloperOptions(enabled)
        }
    }

    fun setAutoDisableAdb(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoDisableAdb(enabled)
        }
    }

    fun setDelaySeconds(delay: Int) {
        viewModelScope.launch {
            settingsRepository.setDelaySeconds(delay)
        }
    }

    fun setShowStatusToast(show: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowStatusToast(show)
        }
    }

    fun enableDeveloperOptions() {
        settingsController.enableDeveloperOptions()
        logRepository.addLog("Manually enabled Developer Options")
        updateLogs()
    }

    fun disableDeveloperOptions() {
        settingsController.disableDeveloperOptions()
        logRepository.addLog("Manually disabled Developer Options")
        updateLogs()
    }

    fun enableAdb() {
        settingsController.enableAdb()
        logRepository.addLog("Manually enabled USB Debugging")
        updateLogs()
    }

    fun disableAdb() {
        settingsController.disableAdb()
        logRepository.addLog("Manually disabled USB Debugging")
        updateLogs()
    }

    fun clearLogs() {
        logRepository.clearLogs()
        _logs.value = emptyList()
    }

    private fun refreshStatus() {
        val devEnabled = settingsController.isDeveloperOptionsEnabled()
        val adbEnabledStatus = settingsController.isAdbEnabled()

        if (_developerOptionsEnabled.value != devEnabled) {
            _developerOptionsEnabled.value = devEnabled
            val status = if (devEnabled) "ENABLED" else "DISABLED"
            logRepository.addLog("Developer Options: $status")
            updateLogs()
        }

        if (_adbEnabled.value != adbEnabledStatus) {
            _adbEnabled.value = adbEnabledStatus
            val status = if (adbEnabledStatus) "ENABLED" else "DISABLED"
            logRepository.addLog("USB Debugging: $status")
            updateLogs()
        }
    }

    private fun updateLogs() {
        _logs.value = logRepository.getLogs()
    }
}
