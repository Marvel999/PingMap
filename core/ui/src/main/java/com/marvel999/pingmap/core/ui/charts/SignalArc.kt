package com.marvel999.pingmap.core.ui.charts

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun SignalArc(
    quality: Int,
    modifier: Modifier = Modifier
) {
    val color = PingMapColors.signalColor(quality)
    val animatedSweep by animateFloatAsState(
        targetValue = quality * 1.8f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "arc"
    )
    val label = when {
        quality >= 70 -> "Excellent"
        quality >= 40 -> "Fair"
        else -> "Poor"
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val strokeWidth = 14.dp.toPx()
            val startAngle = 180f
            drawArc(
                color = Color(0xFFEDF0F5),
                startAngle = startAngle,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = animatedSweep,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = 12.dp)
        ) {
            Text(
                "$quality%",
                style = MaterialTheme.typography.headlineMedium.copy(color = color)
            )
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}
