package com.marvel999.pingmap.feature.speedtest.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestResult

@Composable
fun SpeedTestScreen(viewModel: SpeedTestViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Speed Test", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        SpeedGauge(
            value = state.currentSpeedMbps,
            phase = state.phase,
            result = state.result,
            isRunning = state.isRunning
        )

        Spacer(Modifier.height(24.dp))

        PingMapButton(
            text = if (state.isRunning) "Running..." else "Run Speed Test",
            isLoading = state.isRunning,
            onClick = { viewModel.startTest() },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Speed
        )

        state.error?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftRed)
        }

        state.result?.let { VerdictCard(it) }

        if (state.history.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text("History", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            state.history.take(5).forEach { SpeedHistoryRow(it) }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SpeedGauge(
    value: Double,
    phase: TestPhase,
    result: SpeedTestResult?,
    isRunning: Boolean
) {
    val displayValue = result?.downloadMbps ?: value
    val animatedValue by animateFloatAsState(
        targetValue = displayValue.toFloat(),
        animationSpec = tween(durationMillis = 800), label = "speed"
    )
    PingMapCard {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isRunning && phase != TestPhase.PINGING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp,
                    color = PingMapColors.SoftBlue
                )
                Spacer(Modifier.height(12.dp))
            }
            Text(
                text = if (displayValue > 0) "%.1f".format(animatedValue) else "--",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 48.sp,
                    color = PingMapColors.SoftBlue
                )
            )
            Text("Mbps", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                when (phase) {
                    TestPhase.IDLE -> "Ready"
                    TestPhase.PINGING -> "Measuring ping..."
                    TestPhase.DOWNLOADING -> "↓ Download"
                    TestPhase.UPLOADING -> "↑ Upload"
                    TestPhase.DONE -> "Complete"
                },
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun VerdictCard(result: SpeedTestResult) {
    val (verdict, color) = when {
        result.downloadMbps <= 0 -> "Could not measure speed. Check your connection and try again." to PingMapColors.SoftAmber
        result.downloadMbps >= 50 -> "Great — fast enough for streaming and gaming" to PingMapColors.SoftGreen
        result.downloadMbps >= 25 -> "Good — enough for most tasks" to PingMapColors.SoftGreen
        result.downloadMbps >= 10 -> "Fair — OK for browsing" to PingMapColors.SoftAmber
        else -> "Slow — try moving closer to router" to PingMapColors.SoftRed
    }
    Spacer(Modifier.height(16.dp))
    PingMapCard {
        Text(verdict, style = MaterialTheme.typography.bodyMedium.copy(color = color))
    }
}

@Composable
private fun SpeedHistoryRow(result: SpeedTestResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("↓ ${"%.1f".format(result.downloadMbps)} Mbps", style = MaterialTheme.typography.bodyMedium)
        Text("↑ ${"%.1f".format(result.uploadMbps)} Mbps", style = MaterialTheme.typography.bodyMedium)
        Text("${result.pingMs} ms", style = MaterialTheme.typography.labelSmall)
    }
}
