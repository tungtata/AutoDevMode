package com.tungtata.usbdebugauto.repository

import com.tungtata.usbdebugauto.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogRepository {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun addLog(message: String) {
        val currentLogs = _logs.value.toMutableList()
        currentLogs.add(LogEntry(message = message))
        _logs.value = currentLogs
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun getLogs(): List<LogEntry> = _logs.value
}
