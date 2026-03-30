package com.tungtata.usbdebugauto.controller

import android.provider.Settings
import android.content.Context

class SecureSettingsController(private val context: Context) {
    fun enableDeveloperOptions() {
        try {
            Settings.Secure.putInt(context.contentResolver, "development_settings_enabled", 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disableDeveloperOptions() {
        try {
            Settings.Secure.putInt(context.contentResolver, "development_settings_enabled", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun enableAdb() {
        try {
            Settings.Secure.putInt(context.contentResolver, "adb_enabled", 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disableAdb() {
        try {
            Settings.Secure.putInt(context.contentResolver, "adb_enabled", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isDeveloperOptionsEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(context.contentResolver, "development_settings_enabled", 0) == 1
        } catch (e: Exception) {
            false
        }
    }

    fun isAdbEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(context.contentResolver, "adb_enabled", 0) == 1
        } catch (e: Exception) {
            false
        }
    }
}
