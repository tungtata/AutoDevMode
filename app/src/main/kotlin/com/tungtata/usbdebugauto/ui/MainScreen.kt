package com.tungtata.usbdebugauto.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tungtata.usbdebugauto.R
import com.tungtata.usbdebugauto.viewmodel.MainViewModel

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
    val delaySeconds by viewModel.delaySeconds.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val developerOptionsEnabled by viewModel.developerOptionsEnabled.collectAsState()
    val adbEnabled by viewModel.adbEnabled.collectAsState()
    val showStatusToast by viewModel.showStatusToast.collectAsState()

    val adbCommand = stringResource(R.string.adb_command)

    // Show toast when status changes (if enabled)
    LaunchedEffect(developerOptionsEnabled, adbEnabled, showStatusToast) {
        if (showStatusToast) {
            when (developerOptionsEnabled) {
                true -> Toast.makeText(context, "✓ Developer Options: ENABLED", Toast.LENGTH_SHORT).show()
                false -> Toast.makeText(context, "✗ Developer Options: DISABLED", Toast.LENGTH_SHORT).show()
                else -> {}
            }
            when (adbEnabled) {
                true -> Toast.makeText(context, "✓ USB Debugging: ENABLED", Toast.LENGTH_SHORT).show()
                false -> Toast.makeText(context, "✗ USB Debugging: DISABLED", Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

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
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Current Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Current Status",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Developer Options:",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        when (developerOptionsEnabled) {
                            true -> "✓ ENABLED"
                            false -> "✗ DISABLED"
                            else -> "..."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = when (developerOptionsEnabled) {
                                true -> Color(0xFF4CAF50)
                                false -> Color(0xFFF44336)
                                else -> Color.Gray
                            }
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "USB Debugging:",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        when (adbEnabled) {
                            true -> "✓ ENABLED"
                            false -> "✗ DISABLED"
                            else -> "..."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = when (adbEnabled) {
                                true -> Color(0xFF4CAF50)
                                false -> Color(0xFFF44336)
                                else -> Color.Gray
                            }
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

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
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
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
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Auto-enable options
        SettingCheckbox(
            label = stringResource(R.string.auto_enable_dev_options),
            checked = autoEnableDev,
            onCheckedChange = { viewModel.setAutoEnableDeveloperOptions(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        SettingCheckbox(
            label = stringResource(R.string.auto_enable_adb),
            checked = autoEnableAdb,
            onCheckedChange = { viewModel.setAutoEnableAdb(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Auto-disable options
        SettingCheckbox(
            label = stringResource(R.string.auto_disable_dev_options),
            checked = autoDisableDev,
            onCheckedChange = { viewModel.setAutoDisableDeveloperOptions(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        SettingCheckbox(
            label = stringResource(R.string.auto_disable_adb),
            checked = autoDisableAdb,
            onCheckedChange = { viewModel.setAutoDisableAdb(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Delay Selector
        DelaySelector(
            currentDelay = delaySeconds,
            onDelaySelected = { viewModel.setDelaySeconds(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Show Status Toast Option
        SettingCheckbox(
            label = "Show status toast notifications",
            checked = showStatusToast,
            onCheckedChange = { viewModel.setShowStatusToast(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Manual Controls Section
        Text(
            stringResource(R.string.manual_controls),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
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
                            modifier = Modifier.padding(bottom = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Button(onClick = onCheckClick) {
                    Text(stringResource(R.string.check_permission))
                }
            }

            Text(
                if (permissionGranted) stringResource(R.string.granted) else stringResource(R.string.not_granted),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!permissionGranted) {
                Text(
                    "To grant permission, run this ADB command on your computer while the device is connected:",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground
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
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onBackground
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
    onCheckedChange: (Boolean) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground
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
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun DelaySelector(
    currentDelay: Int,
    onDelaySelected: (Int) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    var expanded by remember { mutableStateOf(false) }
    val delayOptions = listOf(0, 2, 5, 10)

    Column {
        Text(
            stringResource(R.string.delay_seconds),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = textColor
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Text("$currentDelay seconds", color = textColor)
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
