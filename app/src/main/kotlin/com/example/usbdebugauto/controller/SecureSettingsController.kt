package com.example.usbdebugauto.controller

import android.content.Context
import android.provider.Settings
import com.example.usbdebugauto.model.SettingOperationResult
import com.example.usbdebugauto.repository.LogRepository

class SecureSettingsController(
    private val context: Context,
    private val logRepository: LogRepository
) {

    /**
     * Check if the app has been granted WRITE_SECURE_SETTINGS permission via ADB
     * Note: This permission cannot be self-granted and must be given via:
     * adb shell pm grant com.example.usbdebugauto android.permission.WRITE_SECURE_SETTINGS
     */
    fun isWriteSecureSettingsGranted(): Boolean {
        return try {
            context.checkPermission(
                android.Manifest.permission.WRITE_SECURE_SETTINGS,
                android.os.Process.myPid(),
                android.os.Process.myUid()
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            logRepository.addError("Error checking WRITE_SECURE_SETTINGS permission: ${e.message}")
            false
        }
    }

    /**
     * Enable Developer Options by setting development_settings_enabled to 1
     */
    fun setDeveloperOptions(enabled: Boolean): SettingOperationResult {
        return try {
            if (!isWriteSecureSettingsGranted()) {
                val error = "WRITE_SECURE_SETTINGS permission not granted"
                logRepository.addError(error)
                return SettingOperationResult.Error(Exception(error))
            }

            val value = if (enabled) 1 else 0
            Settings.Secure.putInt(context.contentResolver, "development_settings_enabled", value)
            val action = if (enabled) "enabling" else "disabling"
            logRepository.addLog("Successfully $action Developer Options")
            SettingOperationResult.Success
        } catch (e: Exception) {
            val message = "Error setting Developer Options: ${e.message}"
            logRepository.addError(message)
            SettingOperationResult.Error(e)
        }
    }

    /**
     * Enable USB Debugging by setting adb_enabled to 1
     */
    fun setAdbEnabled(enabled: Boolean): SettingOperationResult {
        return try {
            if (!isWriteSecureSettingsGranted()) {
                val error = "WRITE_SECURE_SETTINGS permission not granted"
                logRepository.addError(error)
                return SettingOperationResult.Error(Exception(error))
            }

            val value = if (enabled) 1 else 0
            Settings.Secure.putInt(context.contentResolver, "adb_enabled", value)
            val action = if (enabled) "enabling" else "disabling"
            logRepository.addLog("Successfully $action USB Debugging (adb_enabled)")
            SettingOperationResult.Success
        } catch (e: Exception) {
            val message = "Error setting USB Debugging: ${e.message}"
            logRepository.addError(message)
            SettingOperationResult.Error(e)
        }
    }

    /**
     * Get current state of Developer Options
     * Returns null if cannot read or permission not granted
     */
    fun getDeveloperOptionsState(): Boolean? {
        return try {
            val value = Settings.Secure.getInt(context.contentResolver, "development_settings_enabled", 0)
            value == 1
        } catch (e: Exception) {
            logRepository.addWarning("Cannot read Developer Options state: ${e.message}")
            null
        }
    }

    /**
     * Get current state of USB Debugging
     * Returns null if cannot read or permission not granted
     */
    fun getAdbState(): Boolean? {
        return try {
            val value = Settings.Secure.getInt(context.contentResolver, "adb_enabled", 0)
            value == 1
        } catch (e: Exception) {
            logRepository.addWarning("Cannot read USB Debugging state: ${e.message}")
            null
        }
    }
}
