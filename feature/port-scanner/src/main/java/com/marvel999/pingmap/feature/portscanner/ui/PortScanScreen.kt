package com.marvel999.pingmap.feature.portscanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SectionHeader
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.portscanner.domain.PortResult

@Composable
fun PortScanScreen(viewModel: PortScanViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .padding(20.dp)
    ) {
        SectionHeader("Port Scanner", "Find open ports on any device")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.host,
            onValueChange = viewModel::setHost,
            label = { Text("Hostname or IP") },
            placeholder = { Text("e.g. 192.168.1.1") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PingMapButton(
            text = if (state.isScanning) "Scanning..." else "Start Scan",
            isLoading = state.isScanning,
            onClick = { viewModel.startScan() },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.NetworkCheck
        )

        state.error?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftRed)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "${state.openPorts.size} open port(s)",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        if (state.openPorts.isEmpty() && !state.isScanning) {
            PingMapCard {
                Text(
                    "Enter an IP or hostname and tap Start Scan. Common ports 1–1024 will be checked.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.openPorts) { result ->
                    PortRow(result)
                }
            }
        }
    }
}

@Composable
private fun PortRow(result: PortResult) {
    PingMapCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Port ${result.port}", style = MaterialTheme.typography.titleSmall)
            Text(result.serviceName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
