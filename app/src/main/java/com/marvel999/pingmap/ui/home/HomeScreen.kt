package com.marvel999.pingmap.ui.home

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.charts.SignalArc
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SignalStrengthBar
import com.marvel999.pingmap.core.ui.navigation.NavTab
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startMonitoring()
        } else {
            viewModel.setNotificationDeniedMessage(
                "Notifications are off — background checks won't notify you."
            )
        }
    }

    fun onMonitoringToggle(enabled: Boolean) {
        if (enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.startMonitoring()
            }
        } else {
            viewModel.stopMonitoring()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Your Network", style = MaterialTheme.typography.headlineMedium)
        Text(
            state.networkStatusMessage,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(Modifier.height(16.dp))

        PingMapCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SignalArc(quality = state.signalQuality, modifier = Modifier.size(100.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.ssid, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${state.band} · ${state.security}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Based on your current WiFi signal",
                        style = MaterialTheme.typography.labelSmall,
                        color = PingMapColors.TextSecondary
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                LabeledValue("IP", state.localIp)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PingMapCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.lastDownloadMbps?.let { "%.1f".format(it) } ?: "--",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PingMapColors.SoftBlue
                    )
                    Text("Download Mbps", style = MaterialTheme.typography.labelSmall)
                }
            }
            PingMapCard(modifier = Modifier.weight(1f)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.lastUploadMbps?.let { "%.1f".format(it) } ?: "--",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PingMapColors.LavenderPurple
                    )
                    Text("Upload Mbps", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        PingMapCard {
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            PingMapButton(
                "Run Speed Test",
                onClick = { onNavigate(NavTab.SPEED.route) },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Speed
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onNavigate(NavTab.WIFI.route) }, modifier = Modifier.weight(1f)) {
                    Text("Scan WiFi")
                }
                OutlinedButton(onClick = { onNavigate(NavTab.TOOLS.route) }, modifier = Modifier.weight(1f)) {
                    Text("Ping Test")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        PingMapCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Icon(
                    Icons.Outlined.DevicesOther,
                    contentDescription = null,
                    tint = PingMapColors.SoftBlue
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "${state.deviceCount} devices on this network",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { onNavigate(NavTab.DEVICES.route) }) { Text("See all") }
            }
        }

        Spacer(Modifier.height(16.dp))

        PingMapCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Background monitoring", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Get notified when you lose internet (runs in background)",
                        style = MaterialTheme.typography.bodySmall,
                        color = PingMapColors.TextSecondary
                    )
                }
                Switch(
                    checked = state.backgroundMonitoringEnabled,
                    onCheckedChange = { onMonitoringToggle(it) }
                )
            }
            state.notificationDeniedMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = PingMapColors.SoftAmber
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
