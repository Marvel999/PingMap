package com.marvel999.pingmap.core.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.marvel999.pingmap.core.ui.theme.PingMapColors

@Composable
fun PingMapBottomNav(currentRoute: String, onTabSelected: (NavTab) -> Unit) {
    NavigationBar(
        containerColor = PingMapColors.White,
        tonalElevation = 0.dp
    ) {
        NavTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PingMapColors.SoftBlue,
                    selectedTextColor = PingMapColors.SoftBlue,
                    unselectedIconColor = PingMapColors.TextHint,
                    unselectedTextColor = PingMapColors.TextHint,
                    indicatorColor = PingMapColors.LightBlue
                )
            )
        }
    }
}
