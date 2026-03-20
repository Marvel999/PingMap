package com.marvel999.pingmap.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PingMapColors.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        PingMapCard {
            SettingSwitch(
                title = "Auto-scan on open",
                subtitle = "Automatically scan when app is opened",
                checked = state.autoScanOnOpen,
                onCheckedChange = viewModel::setAutoScanOnOpen
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            SettingSwitch(
                title = "Background monitoring",
                subtitle = "Foreground notification and periodic checks while enabled (uses battery)",
                checked = state.backgroundMonitoringEnabled,
                onCheckedChange = viewModel::setBackgroundMonitoringEnabled
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            SettingSwitch(
                title = "Warn if unsafe network",
                subtitle = "Notify if your connected network score is below 20",
                checked = state.warnUnsafeNetwork,
                onCheckedChange = viewModel::setWarnUnsafeNetwork
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            SettingSwitch(
                title = "Show expert details",
                subtitle = "Expose raw technical panel on Network Detail",
                checked = state.showExpertDetails,
                onCheckedChange = viewModel::setShowExpertDetails
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            SettingSwitch(
                title = "Save scan history",
                subtitle = "Store scan results locally",
                checked = state.saveScanHistory,
                onCheckedChange = viewModel::setSaveScanHistory
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            SettingSwitch(
                title = "Run speed test auto",
                subtitle = "Auto-run speed test after every scan (uses data)",
                checked = state.runSpeedTestAuto,
                onCheckedChange = viewModel::setRunSpeedTestAuto
            )
        }
        Spacer(Modifier.height(12.dp))
        PingMapCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Language", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (state.language == "en") "English" else state.language,
                        style = MaterialTheme.typography.bodySmall,
                        color = PingMapColors.TextSecondary
                    )
                }
                Text("English (v1)", style = MaterialTheme.typography.bodyMedium, color = PingMapColors.TextSecondary)
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextSecondary)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
