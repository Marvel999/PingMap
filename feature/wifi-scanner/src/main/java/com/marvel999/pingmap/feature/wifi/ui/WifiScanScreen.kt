package com.marvel999.pingmap.feature.wifi.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SectionHeader
import com.marvel999.pingmap.core.ui.components.SignalStrengthBar
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.wifi.domain.WifiNetwork

@Composable
fun WifiScanScreen(
    viewModel: WifiScanViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.startScan()
    }

    fun onScanClick() {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.startScan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
    ) {
        if (!hasLocationPermission && !state.isScanning && state.networks.isEmpty()) {
            PingMapCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "Location permission is required to scan WiFi networks (Android requirement). Tap Scan to allow.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("WiFi Networks", style = MaterialTheme.typography.headlineMedium)
                Text(
                    if (state.isScanning) "Scanning..." else "${state.networks.size} networks found",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { onScanClick() }, enabled = !state.isScanning) {
                if (state.isScanning) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Scan")
                }
            }
        }

        state.errorMessage?.let { msg ->
            Text(
                msg,
                style = MaterialTheme.typography.bodyMedium,
                color = PingMapColors.SoftRed,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.networks.isEmpty() && !state.isScanning) {
                item {
                    PingMapCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = true) { onScanClick() }
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Tap here or the refresh button to scan",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(state.networks) { network ->
                    WifiNetworkItem(network)
                }
                if (state.channelCongestion.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        SectionHeader("Channel congestion", "Fewer networks = better")
                        state.channelCongestion.toList().sortedBy { it.first }.forEach { (ch, count) ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ch $ch", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.bodyMedium)
                                Text("$count network(s)", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WifiNetworkItem(network: WifiNetwork) {
    PingMapCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Wifi,
                contentDescription = null,
                tint = PingMapColors.signalColor(network.signalQuality),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(network.ssid, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${bandFromFreq(network.frequency)} · Ch ${network.channel} · ${network.security}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            SignalStrengthBar(network.signalQuality)
            Spacer(Modifier.width(4.dp))
            Text("${network.signalQuality}%", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun bandFromFreq(freq: Int): String = when {
    freq in 2412..2484 -> "2.4 GHz"
    freq in 5170..5825 -> "5 GHz"
    else -> "?"
}
