package com.marvel999.pingmap.feature.signalmap.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.marvel999.pingmap.feature.signalmap.domain.SignalPoint

@Composable
fun SignalMapScreen(viewModel: SignalMapViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .padding(20.dp)
    ) {
        SectionHeader("Signal Map", "Walk and record WiFi coverage")
        Spacer(Modifier.height(16.dp))

        PingMapButton(
            text = "Record point",
            onClick = { viewModel.recordPoint() },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Add
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.startNewSession() },
                modifier = Modifier.weight(1f)
            ) {
                Text("New session")
            }
            OutlinedButton(
                onClick = { viewModel.clearSession() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        state.error?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftRed)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "${state.points.size} point(s) recorded",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        if (state.points.isEmpty()) {
            PingMapCard {
                Text(
                    "Connect to Wi‑Fi, enable location, then tap Record point as you move. Points will show here (full map in a later update).",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.points) { point ->
                    PointRow(point)
                }
            }
        }
    }
}

@Composable
private fun PointRow(point: SignalPoint) {
    PingMapCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "%.5f, %.5f".format(point.lat, point.lng),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(point.ssid.ifEmpty { "—" }, style = MaterialTheme.typography.labelSmall)
            }
            Text("${point.rssi} dBm", style = MaterialTheme.typography.titleSmall)
        }
    }
}
