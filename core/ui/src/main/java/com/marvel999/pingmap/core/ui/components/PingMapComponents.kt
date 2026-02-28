package com.marvel999.pingmap.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun PingMapCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PingMapColors.OffWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, PingMapColors.LightGray)
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
fun SignalStrengthBar(quality: Int, modifier: Modifier = Modifier) {
    val color = PingMapColors.signalColor(quality)
    val filledBars = when {
        quality >= 80 -> 5
        quality >= 60 -> 4
        quality >= 40 -> 3
        quality >= 20 -> 2
        else -> 1
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        (1..5).forEach { bar ->
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height((bar * 5 + 5).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (bar <= filledBars) color else PingMapColors.LightGray)
            )
        }
    }
}

@Composable
fun StatusChip(label: String, color: Color, backgroundColor: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (subtitle != null) {
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun PingMapButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PingMapColors.SoftBlue),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
    }
}

@Composable
fun MetricTile(
    value: String,
    unit: String,
    label: String,
    color: Color = PingMapColors.SoftBlue
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.displayLarge.copy(color = color))
            Spacer(Modifier.width(4.dp))
            Text(
                unit,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall)
    }
}
