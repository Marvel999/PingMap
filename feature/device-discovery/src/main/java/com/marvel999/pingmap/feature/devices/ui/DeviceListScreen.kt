package com.marvel999.pingmap.feature.devices.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.devices.domain.NetworkDevice

@Composable
fun DeviceListScreen(viewModel: DeviceDiscoveryViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Devices on Network", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${state.devices.size} found" + if (state.subnet.isNotEmpty()) " · ${state.subnet}" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = { viewModel.startScan() }, enabled = !state.isScanning) {
                if (state.isScanning) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Scan")
                }
            }
        }

        state.error?.let { msg ->
            Text(
                msg,
                style = MaterialTheme.typography.bodyMedium,
                color = PingMapColors.SoftRed,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        if (state.devices.isEmpty() && !state.isScanning) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.DevicesOther,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = PingMapColors.TextHint
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Tap the refresh button to scan for devices",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.devices) { device ->
                    DeviceItem(device)
                }
            }
        }
    }
}

@Composable
private fun DeviceItem(device: NetworkDevice) {
    PingMapCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.DevicesOther,
                contentDescription = null,
                tint = if (device.isCurrentDevice) PingMapColors.SoftBlue else PingMapColors.TextHint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    device.hostname ?: device.ipAddress,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(device.ipAddress, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${device.manufacturer} · ${device.macAddress.take(12)}...",
                    style = MaterialTheme.typography.labelSmall
                )
                if (device.isCurrentDevice) {
                    Text("This device", style = MaterialTheme.typography.labelSmall, color = PingMapColors.SoftBlue)
                }
            }
        }
    }
}
