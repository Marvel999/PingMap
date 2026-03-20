package com.marvel999.pingmap.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.components.PingMapCard
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PingMapColors.White)
    ) {
        TopAppBar(
            title = { Text("Scan history", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PingMapColors.White,
                titleContentColor = PingMapColors.TextPrimary
            )
        )
        if (state.sessions.isEmpty()) {
            PingMapCard(modifier = Modifier.padding(20.dp)) {
                Text(
                    "No scan history yet. Run a scan on Home to see sessions here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PingMapColors.TextSecondary
                )
            }
        } else {
            val grouped = state.sessions.groupBy { formatSessionDate(it.session.timestamp) }
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                grouped.forEach { (date, sessions) ->
                    item {
                        Text(
                            date,
                            style = MaterialTheme.typography.labelMedium,
                            color = PingMapColors.TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(sessions) { display ->
                        SessionRow(
                            display = display,
                            onClick = { viewModel.openDetail(display) }
                        )
                    }
                }
            }
        }
    }

    state.detailSession?.let { display ->
        HistoryDetailSheet(
            display = display,
            networks = state.detailSessionNetworks,
            onDismiss = { viewModel.dismissDetail() }
        )
    }
}

@Composable
private fun SessionRow(display: SessionDisplay, onClick: () -> Unit) {
    val location = display.session.locationName.ifEmpty { "Scan" }
    val topSsid = display.topNetwork?.ssid ?: "—"
    val badge = display.topNetwork?.badgeText ?: "—"
    PingMapCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column {
            Text(location, style = MaterialTheme.typography.titleMedium)
            Text(
                "${display.networkCount} networks · Top: $topSsid",
                style = MaterialTheme.typography.bodySmall,
                color = PingMapColors.TextSecondary
            )
            Text(
                badge,
                style = MaterialTheme.typography.labelSmall,
                color = PingMapColors.SoftBlue,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryDetailSheet(
    display: SessionDisplay,
    networks: List<com.marvel999.pingmap.data.local.entity.ScanSessionNetworkEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                formatSessionDate(display.session.timestamp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "${display.networkCount} networks found",
                style = MaterialTheme.typography.bodySmall,
                color = PingMapColors.TextSecondary
            )
            Text(
                "Networks in this scan:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            networks.forEach { n ->
                PingMapCard(modifier = Modifier.padding(vertical = 4.dp)) {
                    Column {
                        Text(n.ssid, style = MaterialTheme.typography.bodyMedium)
                        Text("Score ${n.totalScore} · ${n.badgeText}", style = MaterialTheme.typography.labelSmall, color = PingMapColors.TextSecondary)
                    }
                }
            }
            Spacer(Modifier.padding(32.dp))
        }
    }
}
