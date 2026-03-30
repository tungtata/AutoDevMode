package com.tungtata.usbdebugauto.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Info
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
import com.tungtata.usbdebugauto.DarkModePreference
import com.tungtata.usbdebugauto.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, context: Context) {
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

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
    val darkModePreference by viewModel.darkModePreference.collectAsState()

    var showAboutDialog by remember { mutableStateOf(false) }

    val adbCommand = stringResource(R.string.adb_command)

    LaunchedEffect(developerOptionsEnabled, adbEnabled) {
        when (developerOptionsEnabled) {
            true -> Toast.makeText(context, "✅ Developer Options: ENABLED", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(context, "❌ Developer Options: DISABLED", Toast.LENGTH_SHORT).show()
            else -> {}
        }
        when (adbEnabled) {
            true -> Toast.makeText(context, "✅ USB Debugging: ENABLED", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(context, "❌ USB Debugging: DISABLED", Toast.LENGTH_SHORT).show()
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            "🔧 USB Debug Auto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Smart automation for developer tools",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Current Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Current Status",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                StatusRow(
                    label = "Developer Options",
                    isEnabled = developerOptionsEnabled
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow(
                    label = "USB Debugging",
                    isEnabled = adbEnabled
                )
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

        Spacer(modifier = Modifier.height(20.dp))

        // Automation Master Switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .clickable { viewModel.setAutomationEnabled(!automationEnabled) },
            colors = CardDefaults.cardColors(
                containerColor = if (automationEnabled) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Automation",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = if (automationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (automationEnabled) "ON" else "OFF",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (automationEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Settings Section
        Text(
            "⚡ Automation Settings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        SettingToggle(
            label = "Auto-enable Developer Options",
            isEnabled = autoEnableDev,
            onToggle = { viewModel.setAutoEnableDeveloperOptions(it) }
        )

        SettingToggle(
            label = "Auto-enable USB Debugging",
            isEnabled = autoEnableAdb,
            onToggle = { viewModel.setAutoEnableAdb(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingToggle(
            label = "Auto-disable Dev Options on disconnect",
            isEnabled = autoDisableDev,
            onToggle = { viewModel.setAutoDisableDeveloperOptions(it) }
        )

        SettingToggle(
            label = "Auto-disable USB Debugging on disconnect",
            isEnabled = autoDisableAdb,
            onToggle = { viewModel.setAutoDisableAdb(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Delay Selector
        DelaySelector(
            currentDelay = delaySeconds,
            onDelaySelected = { viewModel.setDelaySeconds(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dark Mode Selector
        DarkModeSelector(
            currentMode = darkModePreference,
            onModeSelected = { viewModel.setDarkModePreference(it) },
            textColor = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Controls Section
        Text(
            "🎮 Manual Controls",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp),
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
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("✓ Enable Dev", fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.disableDeveloperOptions() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("✗ Disable Dev", fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.enableAdb() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("✓ Enable Debug", fontSize = 12.sp)
            }
            Button(
                onClick = { viewModel.disableAdb() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("✗ Disable Debug", fontSize = 12.sp)
            }
        }

        // Logs Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📋 Activity Log",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { viewModel.clearLogs() },
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(8.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Clear", fontSize = 11.sp)
            }
        }

        // Logs Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (logs.isEmpty()) {
                    Text(
                        "No activity yet. Waiting for events...",
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

        Spacer(modifier = Modifier.height(20.dp))

        // Footer - About and GitHub
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showAboutDialog = true },
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp, 0.dp)
            ) {
                Text("About", fontSize = 13.sp)
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = android.net.Uri.parse("https://github.com/tungtata/AutoDevMode")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(12.dp, 0.dp)
            ) {
                Text("GitHub", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    "ℹ️ About USB Debug Auto",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    "USB Debug Auto v1.0\n\n" +
                            "Automatically enable/disable Developer Options and USB Debugging when USB connection is detected.\n\n" +
                            "✨ Features:\n" +
                            "• No root required\n" +
                            "• Requires WRITE_SECURE_SETTINGS permission\n" +
                            "• Works even when app is closed\n" +
                            "• Customizable automation rules\n" +
                            "• Beautiful Material 3 design\n\n" +
                            "🔗 GitHub: github.com/tungtata/AutoDevMode",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Got it!")
                }
            }
        )
    }
}

@Composable
fun StatusRow(
    label: String,
    isEnabled: Boolean?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            when (isEnabled) {
                true -> "ENABLED"
                false -> "DISABLED"
                else -> "Loading..."
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = when (isEnabled) {
                true -> Color(0xFF4CAF50)
                false -> Color(0xFFF44336)
                else -> Color(0xFFFFC107)
            }
        )
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
        ),
        shape = RoundedCornerShape(12.dp)
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
                        "🔐 Permission Status",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Button(onClick = onCheckClick, shape = RoundedCornerShape(6.dp)) {
                    Text("Check")
                }
            }

            Text(
                if (permissionGranted) "✅ Permission Granted" else "❌ Permission Not Granted",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (!permissionGranted) {
                Text(
                    "Run this command on your computer:",
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
                    ),
                    shape = RoundedCornerShape(8.dp)
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
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("📋 Copy Command")
                }
            }
        }
    }
}

@Composable
fun SettingToggle(
    label: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = textColor
        )
        Row(
            modifier = Modifier
                .background(
                    color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onToggle(!isEnabled) }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (isEnabled) "ON" else "OFF",
                style = MaterialTheme.typography.labelSmall,
                color = if (isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingCheckbox(
    emoji: String = "",
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
            "$emoji $label".trim(),
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
            "⏱️ Auto-enable Delay",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            color = textColor
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(8.dp))
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
fun DarkModeSelector(
    currentMode: DarkModePreference,
    onModeSelected: (DarkModePreference) -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onBackground
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "🌙 Dark Mode",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp),
            color = textColor
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DarkModeButton(
                label = "☀️ Light",
                isSelected = currentMode == DarkModePreference.LIGHT,
                onClick = { onModeSelected(DarkModePreference.LIGHT) },
                modifier = Modifier.weight(1f)
            )

            DarkModeButton(
                label = "🌓 Auto",
                isSelected = currentMode == DarkModePreference.AUTO,
                onClick = { onModeSelected(DarkModePreference.AUTO) },
                modifier = Modifier.weight(1f)
            )

            DarkModeButton(
                label = "🌙 Dark",
                isSelected = currentMode == DarkModePreference.DARK,
                onClick = { onModeSelected(DarkModePreference.DARK) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DarkModeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun SelectionContainer(content: @Composable () -> Unit) {
    Box(content = { content() })
}
