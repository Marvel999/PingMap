package com.marvel999.pingmap.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.charts.SignalArc
import com.marvel999.pingmap.core.ui.components.PingMapButton
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.components.StatusChip
import com.marvel999.pingmap.core.ui.navigation.NavTab
import com.marvel999.pingmap.core.ui.theme.PingMapColors
import com.marvel999.pingmap.navigation.AppRoutes
import com.marvel999.pingmap.feature.wifi.domain.Badge
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val hasLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        viewModel.startScan()
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun onScan() {
        if (!hasLocation) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        else viewModel.startScan()
    }

    LaunchedEffect(Unit) { viewModel.onHomeVisible() }

    val currentScored = state.scoredNetworks.find { it.network.ssid == state.ssid }

    Column(modifier = Modifier.fillMaxSize().background(PingMapColors.OffWhite)) {
        TopAppBar(
            title = {
                Column {
                    Text("WiFi Advisor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = when {
                            state.isScanning -> "Scanning…"
                            !hasLocation -> "Location needed to scan"
                            !state.autoScanOnOpen -> "Auto-scan off — tap refresh to scan"
                            else -> "Find the best network for you"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = PingMapColors.TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { onScan() },
                    enabled = !state.isScanning,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    if (state.isScanning) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = PingMapColors.SoftBlue)
                    } else {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Scan for WiFi networks")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PingMapColors.White,
                titleContentColor = PingMapColors.TextPrimary
            )
        )

        state.unsafeWarningMessage?.let { msg ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = PingMapColors.SoftRed.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = PingMapColors.SoftRed, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextPrimary, modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.dismissUnsafeWarning() }) { Text("Dismiss") }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HeroHintCard(
                    message = state.networkStatusMessage,
                    hasNetworks = state.scoredNetworks.isNotEmpty(),
                    isScanning = state.isScanning
                )
            }

            if (state.ssid != "Not connected") {
                item {
                    ConnectionCard(
                        ssid = state.ssid,
                        signalQuality = state.signalQuality,
                        band = state.band,
                        security = state.security,
                        scored = currentScored
                    )
                }
            }

            val best = state.scoredNetworks.firstOrNull()
            val showBestBanner = best != null && (state.ssid == "Not connected" || best.score.total >= 80)
            if (showBestBanner && best != null) {
                item {
                    BestChoiceCard(
                        best = best,
                        onSeeDetails = { viewModel.openDetail(best) }
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = PingMapColors.LightGray)
            }

            item {
                HomeSubsectionTitle(
                    title = "Tools & history",
                    subtitle = "Devices on network, utilities, and past scans"
                )
            }

            item {
                UtilityNavCard(
                    items = listOf(
                        UtilityNavItem(
                            Icons.Outlined.DevicesOther,
                            PingMapColors.LavenderPurple,
                            "Devices",
                            "What’s on this network",
                            AppRoutes.DEVICES
                        ),
                        UtilityNavItem(
                            Icons.Outlined.Build,
                            PingMapColors.SoftAmber,
                            "Tools",
                            "Ping, ports, signal map",
                            AppRoutes.TOOLS
                        ),
                        UtilityNavItem(
                            Icons.Outlined.History,
                            PingMapColors.TextSecondary,
                            "Scan history",
                            "Past scans & scores",
                            AppRoutes.HISTORY
                        )
                    ),
                    onNavigate = onNavigate
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                HomeSubsectionTitle(
                    title = "Quick actions",
                    subtitle = "Speed test and full WiFi scanner"
                )
            }

            item {
                UtilityNavCard(
                    items = listOf(
                        UtilityNavItem(
                            Icons.Outlined.Speed,
                            PingMapColors.SoftBlue,
                            "Speed test",
                            "Check download & upload",
                            NavTab.SPEED.route
                        ),
                        UtilityNavItem(
                            Icons.Outlined.Wifi,
                            PingMapColors.SoftGreen,
                            "WiFi scanner",
                            "Full list & channel view",
                            NavTab.WIFI.route
                        )
                    ),
                    onNavigate = onNavigate
                )
            }
        }
    }

    state.detailNetwork?.let { scored ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.dismissDetail() },
            sheetState = sheetState
        ) {
            NetworkDetailSheet(
                scored = scored,
                showExpertDetails = state.showExpertDetails,
                connectMessage = state.connectMessage,
                onDismiss = { scope.launch { sheetState.hide() }.invokeOnCompletion { viewModel.dismissDetail() } },
                onConnect = { viewModel.connectToNetwork(scored) },
                onClearConnectMessage = { viewModel.clearConnectMessage() }
            )
        }
    }
}

private data class UtilityNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconTint: androidx.compose.ui.graphics.Color,
    val title: String,
    val subtitle: String,
    val route: String
)

@Composable
private fun HeroHintCard(message: String, hasNetworks: Boolean, isScanning: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PingMapColors.LightBlue.copy(alpha = 0.55f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Which WiFi should you use?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PingMapColors.TextPrimary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = when {
                    isScanning -> "Looking for networks nearby…"
                    hasNetworks -> message.ifBlank { "Your list is sorted by score — best options first." }
                    else -> "Tap the refresh button above to scan. We’ll highlight the safest, fastest choice."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = PingMapColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ConnectionCard(
    ssid: String,
    signalQuality: Int,
    band: String,
    security: String,
    scored: ScoredNetwork?
) {
    PingMapCard {
        Text("You’re connected", style = MaterialTheme.typography.labelMedium, color = PingMapColors.TextSecondary)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SignalArc(quality = signalQuality, modifier = Modifier.size(76.dp))
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ssid, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("$band · $security", style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                if (scored != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StatusChip(scored.score.securityLabel, PingMapColors.TextPrimary, PingMapColors.LightGray)
                        StatusChip(scored.score.speedLabel, PingMapColors.TextPrimary, PingMapColors.LightGray)
                        StatusChip(scored.score.congestionLabel, PingMapColors.TextPrimary, PingMapColors.LightGray)
                    }
                } else {
                    Text("Run a scan to score this network", style = MaterialTheme.typography.labelSmall, color = PingMapColors.TextHint)
                }
            }
        }
    }
}

@Composable
private fun BestChoiceCard(best: ScoredNetwork, onSeeDetails: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PingMapColors.SoftGreen.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Wifi, contentDescription = null, tint = PingMapColors.SoftGreen, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Text("TOP PICK", style = MaterialTheme.typography.labelMedium, color = PingMapColors.SoftGreen, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(10.dp))
            Text(best.network.ssid, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(best.score.headline, style = MaterialTheme.typography.bodyMedium, color = PingMapColors.TextSecondary)
            Spacer(Modifier.height(14.dp))
            PingMapButton(text = "See details & connect", onClick = onSeeDetails, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun HomeSubsectionTitle(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextSecondary)
    }
}

@Composable
private fun UtilityNavCard(items: List<UtilityNavItem>, onNavigate: (String) -> Unit) {
    PingMapCard {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(color = PingMapColors.LightGray, modifier = Modifier.padding(vertical = 4.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigate(item.route) }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                    Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextSecondary)
                }
                Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = PingMapColors.TextHint, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun NetworkDetailSheet(
    scored: ScoredNetwork,
    showExpertDetails: Boolean,
    connectMessage: String?,
    onDismiss: () -> Unit,
    onConnect: () -> Unit,
    onClearConnectMessage: () -> Unit
) {
    var expertExpanded by remember { mutableStateOf(false) }
    val n = scored.network
    val s = scored.score
    val badgeColor = when (s.badge) {
        Badge.BEST_CHOICE -> PingMapColors.SoftGreen
        Badge.GOOD -> PingMapColors.SoftBlue
        Badge.AVERAGE -> PingMapColors.SoftAmber
        Badge.POOR -> PingMapColors.SoftAmber
        Badge.AVOID -> PingMapColors.SoftRed
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(n.ssid, style = MaterialTheme.typography.headlineSmall)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            SignalArc(quality = s.total, modifier = Modifier.size(72.dp))
            Spacer(Modifier.width(16.dp))
            Text("${s.total}/100", style = MaterialTheme.typography.titleLarge, color = badgeColor)
        }
        Spacer(Modifier.height(16.dp))
        DetailRow(icon = "Safety", label = s.securityLabel, sub = "What does this mean? — ${s.securityLabel}. Standard protection for home/office.")
        DetailRow(icon = "Speed", label = s.speedLabel, sub = "Uses ${if (n.frequency in 5170..5825) "5 GHz — better for video calls" else "2.4 GHz"}.")
        DetailRow(icon = "Crowding", label = s.congestionLabel, sub = "Networks sharing this channel: from scan.")
        Spacer(Modifier.height(16.dp))
        PingMapButton(text = "Connect to this network", onClick = onConnect, modifier = Modifier.fillMaxWidth())
        connectMessage?.let { msg ->
            Spacer(Modifier.height(8.dp))
            Text(msg, style = MaterialTheme.typography.bodySmall, color = PingMapColors.SoftBlue)
            TextButton(onClick = onClearConnectMessage) { Text("OK") }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Not now") }
        Spacer(Modifier.height(16.dp))
        if (showExpertDetails) {
            TextButton(onClick = { expertExpanded = !expertExpanded }, modifier = Modifier.fillMaxWidth()) {
                Text(if (expertExpanded) "Hide expert details" else "Show expert details")
            }
            if (expertExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("SSID: ${n.ssid}", style = MaterialTheme.typography.bodySmall)
                    Text("BSSID: ${n.bssid}", style = MaterialTheme.typography.bodySmall)
                    Text("Channel: ${n.channel}", style = MaterialTheme.typography.bodySmall)
                    Text("RSSI: ${n.rssi} dBm", style = MaterialTheme.typography.bodySmall)
                    Text("Frequency: ${n.frequency} MHz", style = MaterialTheme.typography.bodySmall)
                    Text("Security: ${n.security}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun DetailRow(icon: String, label: String, sub: String) {
    Row(modifier = Modifier.padding(vertical = 6.dp)) {
        Text("$icon:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(72.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = PingMapColors.TextSecondary)
        }
    }
}
