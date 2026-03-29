package com.example.usbdebugauto.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.usbdebugauto.R
import com.example.usbdebugauto.model.DetectionMode
import com.example.usbdebugauto.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, context: Context) {
    // Initialize ViewModel
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    // Collect states
    val permissionGranted by viewModel.permissionGranted.collectAsState()
    val automationEnabled by viewModel.automationEnabled.collectAsState()
    val autoEnableDev by viewModel.autoEnableDeveloperOptions.collectAsState()
    val autoEnableAdb by viewModel.autoEnableAdb.collectAsState()
    val autoDisableDev by viewModel.autoDisableDeveloperOptions.collectAsState()
    val autoDisableAdb by viewModel.autoDisableAdb.collectAsState()
    val detectionMode by viewModel.detectionMode.collectAsState()
    val delaySeconds by viewModel.delaySeconds.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val adbCommand = stringResource(R.string.adb_command)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Title
        Text(
            "USB Debug Auto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Permission Status Card
        PermissionStatusCard(
            permissionGranted = permissionGranted,
            adbCommand = adbCommand,
            context = context
        ) {
            viewModel.checkPermission()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Automation Master Switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.enable_automation),
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = automationEnabled,
                    onCheckedChange = { viewModel.setAutomationEnabled(it) }
                )
            }
        }

        // Settings Section
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Auto-enable options
        SettingCheckbox(
            label = stringResource(R.string.auto_enable_dev_options),
            checked = autoEnableDev,
            onCheckedChange = { viewModel.setAutoEnableDeveloperOptions(it) }
        )

        SettingCheckbox(
            label = stringResource(R.string.auto_enable_adb),
            checked = autoEnableAdb,
            onCheckedChange = { viewModel.setAutoEnableAdb(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Auto-disable options
        SettingCheckbox(
            label = stringResource(R.string.auto_disable_dev_options),
            checked = autoDisableDev,
            onCheckedChange = { viewModel.setAutoDisableDeveloperOptions(it) }
        )

        SettingCheckbox(
            label = stringResource(R.string.auto_disable_adb),
            checked = autoDisableAdb,
            onCheckedChange = { viewModel.setAutoDisableAdb(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Detection Mode Selector
        DetectionModeSelector(
            currentMode = detectionMode,
            onModeSelected = { viewModel.setDetectionMode(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Delay Selector
        DelaySelector(
            currentDelay = delaySeconds,
            onDelaySelected = { viewModel.setDelaySeconds(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Manual Controls Section
        Text(
            stringResource(R.string.manual_controls),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.enableDeveloperOptions() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.enable_dev_options), fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.disableDeveloperOptions() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.disable_dev_options), fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.enableAdb() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.enable_adb), fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.disableAdb() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.disable_adb), fontSize = 12.sp)
            }
        }

        // Logs Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.logs),
                style = MaterialTheme.typography.titleMedium
            )
            Button(
                onClick = { viewModel.clearLogs() },
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                Text(stringResource(R.string.clear_logs), fontSize = 11.sp)
            }
        }

        // Logs Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (logs.isEmpty()) {
                    Text(
                        "No logs yet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    logs.forEach { log ->
                        Text(
                            log.toDisplayString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PermissionStatusCard(
    permissionGranted: Boolean,
    adbCommand: String,
    context: Context,
    onCheckClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (permissionGranted) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (permissionGranted) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (permissionGranted) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                    Text(
                        stringResource(R.string.permission_status),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Button(onClick = onCheckClick) {
                    Text(stringResource(R.string.check_permission))
                }
            }

            Text(
                if (permissionGranted) stringResource(R.string.granted) else stringResource(R.string.not_granted),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (!permissionGranted) {
                Text(
                    "To grant permission, run this ADB command on your computer while the device is connected:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    SelectionContainer {
                        Text(
                            adbCommand,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("ADB Command", adbCommand))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.copy_adb_command))
                }
            }
        }
    }
}

@Composable
fun SettingCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun DetectionModeSelector(
    currentMode: DetectionMode,
    onModeSelected: (DetectionMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            stringResource(R.string.detection_mode),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Text(currentMode.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DetectionMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun DelaySelector(
    currentDelay: Int,
    onDelaySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val delayOptions = listOf(0, 2, 5, 10)

    Column {
        Text(
            stringResource(R.string.delay_seconds),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Text("$currentDelay seconds")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            delayOptions.forEach { delay ->
                DropdownMenuItem(
                    text = { Text("$delay seconds") },
                    onClick = {
                        onDelaySelected(delay)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectionContainer(content: @Composable () -> Unit) {
    Box(content = { content() })
}
