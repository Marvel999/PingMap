package com.marvel999.pingmap.core.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun PingLineChart(
    packets: List<Long>,
    modifier: Modifier = Modifier
) {
    if (packets.isEmpty()) return
    val floatValues = packets.map { it.toFloat() }
    val maxVal = floatValues.maxOrNull()?.coerceAtLeast(1f) ?: 1f
    Canvas(modifier = modifier.height(140.dp).fillMaxWidth()) {
        val w = size.width
        val h = size.height
        val pad = 8.dp.toPx()
        val chartW = (w - 2 * pad).coerceAtLeast(1f)
        val chartH = (h - 2 * pad).coerceAtLeast(1f)
        val stepX = if (floatValues.size > 1) chartW / (floatValues.size - 1) else chartW
        floatValues.forEachIndexed { i, v ->
            val x = pad + i * stepX
            val y = pad + chartH - (v / maxVal * chartH)
            if (i == 0) {
                drawCircle(PingMapColors.SoftBlue, radius = 4.dp.toPx(), center = Offset(x, y))
            } else {
                val prevX = pad + (i - 1) * stepX
                val prevY = pad + chartH - (floatValues[i - 1] / maxVal * chartH)
                drawLine(
                    PingMapColors.SoftBlue,
                    Offset(prevX, prevY),
                    Offset(x, y),
                    strokeWidth = 4.dp.toPx()
                )
                drawCircle(PingMapColors.SoftBlue, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}
