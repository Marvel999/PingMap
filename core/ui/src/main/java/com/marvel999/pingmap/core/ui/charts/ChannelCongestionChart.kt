package com.marvel999.pingmap.core.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.StatusChip
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun ChannelCongestionChart(
    congestion: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    if (congestion.isEmpty()) return
    val maxCount = congestion.values.maxOrNull() ?: 1
    val sortedChannels = congestion.entries.sortedBy { it.key }
    var selectedChannel by remember { mutableStateOf<Int?>(null) }

    PingMapCard(modifier = modifier) {
        Text("Channel congestion", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "Fewer networks = better performance. Tap a bar for details.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(16.dp))
        sortedChannels.forEach { (channel, count) ->
            val isBest = count == congestion.values.minOrNull()
            val isSelected = selectedChannel == channel
            val barColor = when {
                count >= 4 -> PingMapColors.SoftRed
                count >= 2 -> PingMapColors.SoftAmber
                else -> PingMapColors.SoftGreen
            }
            Column(
                modifier = Modifier
                    .clickable { selectedChannel = if (selectedChannel == channel) null else channel }
                    .padding(vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Ch $channel",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(44.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(count.toFloat() / maxCount.coerceAtLeast(1))
                            .height(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) barColor.copy(alpha = 0.8f) else barColor
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$count network${if (count != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (isBest) {
                        Spacer(Modifier.width(6.dp))
                        StatusChip("Best", PingMapColors.SoftGreen, PingMapColors.SoftGreen.copy(alpha = 0.2f))
                    }
                }
                if (isSelected) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Ch $channel — $count network(s)${if (isBest) " (recommended)" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PingMapColors.TextSecondary
                    )
                }
            }
        }
    }
}
