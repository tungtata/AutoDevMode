package com.example.usbdebugauto.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.usbdebugauto.model.DetectionMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "usb_debug_auto_prefs"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class SettingsRepository(private val context: Context) {

    private object PreferenceKeys {
        val AUTOMATION_ENABLED = booleanPreferencesKey("automation_enabled")
        val AUTO_ENABLE_DEV_OPTIONS = booleanPreferencesKey("auto_enable_dev_options")
        val AUTO_ENABLE_ADB = booleanPreferencesKey("auto_enable_adb")
        val AUTO_DISABLE_DEV_OPTIONS = booleanPreferencesKey("auto_disable_dev_options")
        val AUTO_DISABLE_ADB = booleanPreferencesKey("auto_disable_adb")
        val DETECTION_MODE = stringPreferencesKey("detection_mode")
        val DELAY_SECONDS = intPreferencesKey("delay_seconds")
    }

    // Flows for observing preferences
    val automationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.AUTOMATION_ENABLED] ?: true
    }

    val autoEnableDeveloperOptions: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.AUTO_ENABLE_DEV_OPTIONS] ?: true
    }

    val autoEnableAdb: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.AUTO_ENABLE_ADB] ?: true
    }

    val autoDisableDeveloperOptions: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.AUTO_DISABLE_DEV_OPTIONS] ?: false
    }

    val autoDisableAdb: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.AUTO_DISABLE_ADB] ?: false
    }

    val detectionMode: Flow<DetectionMode> = context.dataStore.data.map { prefs ->
        val modeString = prefs[PreferenceKeys.DETECTION_MODE] ?: DetectionMode.BALANCED.name
        DetectionMode.valueOf(modeString)
    }

    val delaySeconds: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PreferenceKeys.DELAY_SECONDS] ?: 0
    }

    // Suspend functions for updating preferences
    suspend fun setAutomationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.AUTOMATION_ENABLED] = enabled
        }
    }

    suspend fun setAutoEnableDeveloperOptions(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.AUTO_ENABLE_DEV_OPTIONS] = enabled
        }
    }

    suspend fun setAutoEnableAdb(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.AUTO_ENABLE_ADB] = enabled
        }
    }

    suspend fun setAutoDisableDeveloperOptions(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.AUTO_DISABLE_DEV_OPTIONS] = enabled
        }
    }

    suspend fun setAutoDisableAdb(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.AUTO_DISABLE_ADB] = enabled
        }
    }

    suspend fun setDetectionMode(mode: DetectionMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.DETECTION_MODE] = mode.name
        }
    }

    suspend fun setDelaySeconds(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.DELAY_SECONDS] = seconds
        }
    }
}
