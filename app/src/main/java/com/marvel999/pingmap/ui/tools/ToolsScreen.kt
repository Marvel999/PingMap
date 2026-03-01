package com.marvel999.pingmap.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.SectionHeader
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun ToolsScreen(onNavigate: (String) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .padding(20.dp)
    ) {
        SectionHeader("Tools", "Network diagnostics")
        Spacer(Modifier.height(16.dp))
        ToolCard(
            title = "Ping",
            subtitle = "Test latency to a host",
            icon = Icons.Outlined.NetworkCheck,
            onClick = { onNavigate("ping") }
        )
        Spacer(Modifier.height(12.dp))
        ToolCard(
            title = "Port Scanner",
            subtitle = "Scan open ports on a host",
            icon = Icons.Outlined.Radar,
            onClick = { onNavigate("port_scan") }
        )
        Spacer(Modifier.height(12.dp))
        ToolCard(
            title = "Signal Map",
            subtitle = "Record WiFi coverage by location",
            icon = Icons.Outlined.Map,
            onClick = { onNavigate("signal_map") }
        )
    }
}

@Composable
private fun ToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    PingMapCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = PingMapColors.SoftBlue
            )
            Spacer(Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
