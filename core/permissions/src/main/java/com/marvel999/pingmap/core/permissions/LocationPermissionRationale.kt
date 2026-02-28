package com.marvel999.pingmap.core.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import androidx.compose.material3.Icon

@Composable
fun LocationPermissionRationale(
    onAllow: () -> Unit,
    onDismiss: () -> Unit
) {
    PingMapCard {
        Row {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = PingMapColors.SoftBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Location needed for WiFi scan",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Android requires location access to read nearby WiFi networks. We don't store your location.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Not now")
            }
            PingMapButton("Allow", onClick = onAllow, modifier = Modifier.weight(1f))
        }
    }
}
