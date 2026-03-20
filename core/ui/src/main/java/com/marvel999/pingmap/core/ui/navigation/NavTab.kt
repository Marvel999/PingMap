package com.marvel999.pingmap.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavTab(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val route: String
) {
    HOME(Icons.Outlined.Home, Icons.Filled.Home, "Home", "home"),
    SPEED(Icons.Outlined.Speed, Icons.Filled.Speed, "Speed", "speed_test"),
    WIFI(Icons.Outlined.Wifi, Icons.Filled.Wifi, "WiFi", "wifi_scan"),
    SETTINGS(Icons.Outlined.Settings, Icons.Filled.Settings, "Settings", "settings")
}
