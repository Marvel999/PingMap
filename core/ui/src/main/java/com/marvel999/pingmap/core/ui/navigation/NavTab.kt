package com.marvel999.pingmap.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DevicesOther
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Home
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
    WIFI(Icons.Outlined.Wifi, Icons.Filled.Wifi, "WiFi", "wifi_scan"),
    SPEED(Icons.Outlined.Speed, Icons.Filled.Speed, "Speed", "speed_test"),
    DEVICES(Icons.Outlined.DevicesOther, Icons.Filled.DevicesOther, "Devices", "devices"),
    TOOLS(Icons.Outlined.Build, Icons.Filled.Build, "Tools", "tools")
}
