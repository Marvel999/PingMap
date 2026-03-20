package com.marvel999.pingmap.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var page by remember { mutableIntStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PingMapColors.White)
            .padding(24.dp)
    ) {
        when (page) {
            0 -> OnboardingPage1(
                onSkip = onComplete,
                onNext = { page = 1 }
            )
            1 -> OnboardingPage2(
                onSkip = onComplete,
                onNext = { page = 2 }
            )
            2 -> OnboardingPage3(
                onAllow = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                onSkip = onComplete
            )
        }
    }
}

@Composable
private fun OnboardingPage1(
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PingMapColors.LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Wifi,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PingMapColors.SoftBlue
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(
            "WiFi Advisor picks the best WiFi for you.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "No guesswork — one clear recommendation.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextSecondary
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) {
                Text("Skip")
            }
            PingMapButton(text = "Next", onClick = onNext, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun OnboardingPage2(
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            PillIcon(Icons.Outlined.Security, "Safe", PingMapColors.SoftGreen)
            PillIcon(Icons.Outlined.Speed, "Fast", PingMapColors.SoftBlue)
            PillIcon(Icons.Outlined.Wifi, "Uncrowded", PingMapColors.LavenderPurple)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "We check 3 things: Is it safe? Is it fast? Is it crowded?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Plain English — no technical jargon.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextSecondary
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) {
                Text("Skip")
            }
            PingMapButton(text = "Next", onClick = onNext, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PillIcon(icon: ImageVector, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = tint)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = PingMapColors.TextPrimary)
    }
}

@Composable
private fun OnboardingPage3(
    onAllow: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PingMapColors.LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = PingMapColors.SoftBlue
            )
        }
        Spacer(Modifier.height(40.dp))
        Text(
            "To scan WiFi near you, we need Location access.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextPrimary
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Required by Android to list nearby networks. Your data stays on your phone — we don’t send it anywhere.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = PingMapColors.TextSecondary
        )
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PingMapButton(text = "Allow location", onClick = onAllow, modifier = Modifier.fillMaxWidth())
            TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                Text("Skip for now", color = PingMapColors.TextSecondary)
            }
        }
    }
}
