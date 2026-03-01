package com.marvel999.pingmap.core.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun SpeedHistoryChart(
    downloadHistory: List<Float>,
    uploadHistory: List<Float>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val maxVal = (downloadHistory + uploadHistory).maxOrNull()?.coerceAtLeast(1f) ?: 1f
        Box(modifier = Modifier.height(180.dp).fillMaxWidth()) {
            if (downloadHistory.isNotEmpty() || uploadHistory.isNotEmpty()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val pad = 8.dp.toPx()
                    val chartW = (w - 2 * pad).coerceAtLeast(1f)
                    val chartH = (h - 2 * pad).coerceAtLeast(1f)
                    if (downloadHistory.isNotEmpty()) {
                        val path = Path()
                        val stepX = if (downloadHistory.size > 1) chartW / (downloadHistory.size - 1) else chartW
                        downloadHistory.forEachIndexed { i, v ->
                            val x = pad + i * stepX
                            val y = pad + chartH - (v / maxVal * chartH)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(path, PingMapColors.SoftBlue, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                    }
                    if (uploadHistory.isNotEmpty()) {
                        val path = Path()
                        val stepX = if (uploadHistory.size > 1) chartW / (uploadHistory.size - 1) else chartW
                        uploadHistory.forEachIndexed { i, v ->
                            val x = pad + i * stepX
                            val y = pad + chartH - (v / maxVal * chartH)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(path, PingMapColors.LavenderPurple, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(color = PingMapColors.SoftBlue, label = "Download")
            LegendItem(color = PingMapColors.LavenderPurple, label = "Upload")
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
