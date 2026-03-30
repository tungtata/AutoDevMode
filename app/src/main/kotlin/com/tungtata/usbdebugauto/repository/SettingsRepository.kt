package com.tungtata.usbdebugauto.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.tungtata.usbdebugauto.DarkModePreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        private val PERMISSION_GRANTED = booleanPreferencesKey("permission_granted")
        private val AUTOMATION_ENABLED = booleanPreferencesKey("automation_enabled")
        private val AUTO_ENABLE_DEV = booleanPreferencesKey("auto_enable_dev")
        private val AUTO_ENABLE_ADB = booleanPreferencesKey("auto_enable_adb")
        private val AUTO_DISABLE_DEV = booleanPreferencesKey("auto_disable_dev")
        private val AUTO_DISABLE_ADB = booleanPreferencesKey("auto_disable_adb")
        private val DELAY_SECONDS = intPreferencesKey("delay_seconds")
        private val SHOW_STATUS_TOAST = booleanPreferencesKey("show_status_toast")
        private val DARK_MODE_PREFERENCE = stringPreferencesKey("dark_mode_preference")
    }

    val permissionGranted: Flow<Boolean> = context.dataStore.data
        .map { it[PERMISSION_GRANTED] ?: false }

    val automationEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[AUTOMATION_ENABLED] ?: false }

    val autoEnableDeveloperOptions: Flow<Boolean> = context.dataStore.data
        .map { it[AUTO_ENABLE_DEV] ?: true }

    val autoEnableAdb: Flow<Boolean> = context.dataStore.data
        .map { it[AUTO_ENABLE_ADB] ?: true }

    val autoDisableDeveloperOptions: Flow<Boolean> = context.dataStore.data
        .map { it[AUTO_DISABLE_DEV] ?: false }

    val autoDisableAdb: Flow<Boolean> = context.dataStore.data
        .map { it[AUTO_DISABLE_ADB] ?: false }

    val delaySeconds: Flow<Int> = context.dataStore.data
        .map { it[DELAY_SECONDS] ?: 0 }

    val showStatusToast: Flow<Boolean> = context.dataStore.data
        .map { it[SHOW_STATUS_TOAST] ?: false }

    val darkModePreference: Flow<DarkModePreference> = context.dataStore.data
        .map {
            val value = it[DARK_MODE_PREFERENCE] ?: "AUTO"
            try {
                DarkModePreference.valueOf(value)
            } catch (e: Exception) {
                DarkModePreference.AUTO
            }
        }

    suspend fun setPermissionGranted(granted: Boolean) {
        context.dataStore.edit { it[PERMISSION_GRANTED] = granted }
    }

    suspend fun setAutomationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[AUTOMATION_ENABLED] = enabled }
    }

    suspend fun setAutoEnableDeveloperOptions(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_ENABLE_DEV] = enabled }
    }

    suspend fun setAutoEnableAdb(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_ENABLE_ADB] = enabled }
    }

    suspend fun setAutoDisableDeveloperOptions(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_DISABLE_DEV] = enabled }
    }

    suspend fun setAutoDisableAdb(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_DISABLE_ADB] = enabled }
    }

    suspend fun setDelaySeconds(delay: Int) {
        context.dataStore.edit { it[DELAY_SECONDS] = delay }
    }

    suspend fun setShowStatusToast(show: Boolean) {
        context.dataStore.edit { it[SHOW_STATUS_TOAST] = show }
    }

    suspend fun setDarkModePreference(preference: DarkModePreference) {
        context.dataStore.edit { it[DARK_MODE_PREFERENCE] = preference.name }
    }
}
