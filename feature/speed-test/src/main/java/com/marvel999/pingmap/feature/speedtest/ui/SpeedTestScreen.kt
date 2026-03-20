package com.marvel999.pingmap.feature.speedtest.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.unit.sp
import com.marvel999.pingmap.core.ui.charts.SpeedHistoryChart
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SectionHeader
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestResult

private const val GAUGE_MAX_MBPS = 150f

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
        SectionHeader("Internet Speed Test", "Measure download and upload speed")
        Spacer(Modifier.height(20.dp))

        SpeedometerGauge(
            value = state.currentSpeedMbps,
            phase = state.phase,
            result = state.result,
            isRunning = state.isRunning
        )

        Spacer(Modifier.height(20.dp))

        PingMapButton(
            text = when {
                state.isRunning -> "Checking Internet Speed"
                else -> "Check your internet speed"
            },
            isLoading = state.isRunning,
            onClick = { viewModel.startTest() },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Outlined.Speed
        )

        Spacer(Modifier.height(24.dp))

        SpeedResultsSummary(
            result = state.result,
            liveDownloadMbps = state.liveDownloadMbps,
            liveUploadMbps = state.liveUploadMbps,
            isRunning = state.isRunning
        )

        state.error?.let { msg ->
            Spacer(Modifier.height(12.dp))
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftRed)
        }

        state.result?.let { VerdictCard(it) }

        Spacer(Modifier.height(12.dp))
        ContextBar()

        Spacer(Modifier.height(24.dp))
        SectionHeader("Last 10 tests", "Tap a test to see details")
        Spacer(Modifier.height(8.dp))
        if (state.history.isEmpty() && !state.isRunning) {
            PingMapCard {
                Text(
                    "No speed tests yet. Run a test above to see history here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PingMapColors.TextSecondary
                )
            }
        } else if (state.isRunning && state.history.isEmpty()) {
            PingMapCard {
                Text("Running first test…", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            state.history.take(10).forEach { result ->
                Spacer(Modifier.height(6.dp))
                SpeedTestHistoryRow(
                    result = result,
                    onClick = { viewModel.openDetail(result) }
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    state.detailResult?.let { result ->
        SpeedTestDetailSheet(
            result = result,
            onDismiss = { viewModel.dismissDetail() }
        )
    }
}

@Composable
private fun ContextBar() {
    PingMapCard {
        Text(
            "Netflix HD needs 5 Mbps · Zoom needs 3 Mbps · WhatsApp Video needs 1 Mbps",
            style = MaterialTheme.typography.bodySmall,
            color = PingMapColors.TextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeedTestDetailSheet(result: SpeedTestResult, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Speed test result", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                formatTimeAgo(result.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = PingMapColors.TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Download", style = MaterialTheme.typography.labelSmall)
                    Text("%.1f Mbps".format(result.downloadMbps), style = MaterialTheme.typography.titleLarge, color = PingMapColors.SoftBlue)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Upload", style = MaterialTheme.typography.labelSmall)
                    Text("%.1f Mbps".format(result.uploadMbps), style = MaterialTheme.typography.titleLarge, color = PingMapColors.LavenderPurple)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ping", style = MaterialTheme.typography.labelSmall)
                    Text("${result.pingMs} ms", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(Modifier.height(24.dp))
            val plainLanguage = when {
                result.downloadMbps >= 50 -> "More than enough for HD video calls and gaming."
                result.downloadMbps >= 25 -> "Good for video calls and streaming."
                result.downloadMbps >= 10 -> "Enough for browsing and light video."
                result.downloadMbps > 0 -> "May struggle with video calls."
                else -> "Could not measure."
            }
            Text(plainLanguage, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.TextSecondary)
            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun formatTimeAgo(timestampMs: Long): String {
    val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return sdf.format(Date(timestampMs))
}

private const val TICK_MAJOR_STEP_DEG = 36f   // 5 major ticks (180, 216, 252, 288, 324, 360)
private const val TICK_MINOR_STEP_DEG = 12f   // minor every 12°
private const val START_ANGLE = 180f
private const val SWEEP_ANGLE = 180f

@Composable
private fun SpeedometerGauge(
    value: Double,
    phase: TestPhase,
    result: SpeedTestResult?,
    isRunning: Boolean
) {
    val displayValue = result?.downloadMbps ?: value
    val normalized = (displayValue.toFloat() / GAUGE_MAX_MBPS).coerceIn(0f, 1f)
    val animatedSweep by animateFloatAsState(
        targetValue = normalized * SWEEP_ANGLE,
        animationSpec = tween(durationMillis = 600),
        label = "gauge"
    )
    val phaseLabel = when (phase) {
        TestPhase.IDLE -> "Ready"
        TestPhase.PINGING -> "Ping"
        TestPhase.DOWNLOADING -> "Download"
        TestPhase.UPLOADING -> "Upload"
        TestPhase.DONE -> "Complete"
    }
    val speedText = if (displayValue > 0) "%.1f".format(displayValue) else "--"

    PingMapCard {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(modifier = Modifier.size(300.dp)) {
                val strokeWidth = 24.dp.toPx()
                val cornerRadius = size.minDimension / 2f
                val arcRadius = cornerRadius - strokeWidth / 2f
                val centerX = size.width / 2f
                val centerY = size.height / 2f

                // Track background (full semicircle)
                drawArc(
                    color = PingMapColors.LightGray,
                    startAngle = START_ANGLE,
                    sweepAngle = SWEEP_ANGLE,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        centerX - cornerRadius,
                        centerY - cornerRadius
                    ),
                    size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )

                // Tick marks (outside the arc)
                val tickMinorLen = 6.dp.toPx()
                val tickMajorLen = 12.dp.toPx()
                val tickRadius = arcRadius + strokeWidth / 2f
                var deg = START_ANGLE
                while (deg <= START_ANGLE + SWEEP_ANGLE) {
                    val rad = Math.toRadians(deg.toDouble())
                    val cos = cos(rad).toFloat()
                    val sin = sin(rad).toFloat()
                    val isMajor = (deg - START_ANGLE) % TICK_MAJOR_STEP_DEG < 1f
                    val len = if (isMajor) tickMajorLen else tickMinorLen
                    val from = Offset(centerX + tickRadius * cos, centerY + tickRadius * sin)
                    val to = Offset(centerX + (tickRadius + len) * cos, centerY + (tickRadius + len) * sin)
                    drawLine(
                        color = PingMapColors.SoftBlue.copy(alpha = 0.6f),
                        start = from,
                        end = to,
                        strokeWidth = if (isMajor) 2.5f else 1.5f
                    )
                    deg += TICK_MINOR_STEP_DEG
                }

                // Glow layer (soft blue behind progress)
                drawArc(
                    color = PingMapColors.LightBlue.copy(alpha = 0.5f),
                    startAngle = START_ANGLE,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        centerX - cornerRadius,
                        centerY - cornerRadius
                    ),
                    size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(strokeWidth + 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Progress arc (main)
                drawArc(
                    color = PingMapColors.SoftBlue,
                    startAngle = START_ANGLE,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        centerX - cornerRadius,
                        centerY - cornerRadius
                    ),
                    size = androidx.compose.ui.geometry.Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(strokeWidth, cap = StrokeCap.Round)
                )
            }
            Column(
                modifier = Modifier
                    .offset(y = 80.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    phaseLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = PingMapColors.TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = speedText,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        color = PingMapColors.SoftBlue
                    )
                )
                Text(
                    "mbps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PingMapColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun SpeedResultsSummary(
    result: SpeedTestResult?,
    liveDownloadMbps: Double = 0.0,
    liveUploadMbps: Double = 0.0,
    isRunning: Boolean = false
) {
    val downloadStr = when {
        result != null -> formatMbps(result.downloadMbps)
        isRunning -> formatMbps(liveDownloadMbps)
        else -> "--"
    }
    val uploadStr = when {
        result != null -> formatMbps(result.uploadMbps)
        isRunning -> formatMbps(liveUploadMbps)
        else -> "--"
    }
    val pingStr = if (result != null) "${result.pingMs} ms" else "--"
    val maxStr = when {
        result != null -> formatMbps(maxOf(result.downloadMbps, result.uploadMbps))
        isRunning && (liveDownloadMbps > 0 || liveUploadMbps > 0) -> formatMbps(maxOf(liveDownloadMbps, liveUploadMbps))
        else -> "--"
    }

    PingMapCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "DOWNLOAD SPEED",
                    style = MaterialTheme.typography.labelSmall,
                    color = PingMapColors.TextSecondary
                )
                Text(
                    downloadStr,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = PingMapColors.SoftBlue
                    )
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "PING",
                    style = MaterialTheme.typography.labelSmall,
                    color = PingMapColors.TextSecondary
                )
                Text(
                    pingStr,
                    style = MaterialTheme.typography.titleMedium,
                    color = PingMapColors.TextPrimary
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(72.dp)
                    .background(PingMapColors.LightGray)
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "UPLOAD SPEED",
                    style = MaterialTheme.typography.labelSmall,
                    color = PingMapColors.TextSecondary
                )
                Text(
                    uploadStr,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = PingMapColors.SoftBlue
                    )
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "MAX SPEED",
                    style = MaterialTheme.typography.labelSmall,
                    color = PingMapColors.TextSecondary
                )
                Text(
                    maxStr,
                    style = MaterialTheme.typography.titleMedium,
                    color = PingMapColors.TextPrimary
                )
            }
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
private fun SpeedTestHistoryRow(result: SpeedTestResult, onClick: () -> Unit) {
    PingMapCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(formatTimeAgo(result.timestamp), style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("↓ ${"%.1f".format(result.downloadMbps)}", style = MaterialTheme.typography.bodyMedium, color = PingMapColors.SoftBlue)
                Text("↑ ${"%.1f".format(result.uploadMbps)}", style = MaterialTheme.typography.bodyMedium, color = PingMapColors.LavenderPurple)
            }
        }
    }
}

private fun formatMbps(value: Double): String {
    if (value.isNaN() || value.isInfinite() || value < 0) return "--"
    if (value == 0.0) return "0.0"
    return "%.1f".format(value)
}
