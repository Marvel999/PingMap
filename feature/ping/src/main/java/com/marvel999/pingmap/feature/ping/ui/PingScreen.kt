package com.marvel999.pingmap.feature.ping.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.charts.PingLineChart
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SectionHeader
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.ping.domain.PingResult

@Composable
fun PingScreen(viewModel: PingViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PingMapColors.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        SectionHeader("Ping Test", "Check connection to any address")
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.host,
            onValueChange = viewModel::setHost,
            label = { Text("Hostname or IP") },
            placeholder = { Text("e.g. google.com or 8.8.8.8") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        PingMapButton(
            text = if (state.isRunning) "Pinging..." else "Start Ping",
            isLoading = state.isRunning,
            onClick = { viewModel.startPing() },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.PlayArrow
        )

        state.error?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftRed)
        }

        state.result?.let { result ->
            ResultCard(result)
            if (result.packets.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("RTT over time", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                PingLineChart(packets = result.packets, modifier = Modifier.fillMaxWidth())
            }
        }
        if (state.result == null && !state.isRunning) {
            PingMapCard {
                Text(
                    "Enter a host and tap Start Ping to see latency and a chart.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PingMapColors.TextSecondary
                )
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun ResultCard(r: PingResult) {
    Spacer(Modifier.height(24.dp))
    PingMapCard {
        Text("${r.host}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricChip("${r.minMs}", "Min ms", PingMapColors.SoftGreen)
            MetricChip("${r.avgMs}", "Avg ms", PingMapColors.SoftBlue)
            MetricChip("${r.maxMs}", "Max ms", PingMapColors.SoftAmber)
            MetricChip("${r.packetLoss}%", "Loss", if (r.packetLoss == 0) PingMapColors.SoftGreen else PingMapColors.SoftRed)
        }
        if (r.packets.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Jitter: %.1f ms".format(r.jitterMs),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MetricChip(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
