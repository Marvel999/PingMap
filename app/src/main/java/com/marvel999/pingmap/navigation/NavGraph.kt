package com.marvel999.pingmap.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marvel999.pingmap.core.ui.navigation.NavTab
import com.marvel999.pingmap.core.ui.navigation.PingMapBottomNav
import com.marvel999.pingmap.ui.home.HomeScreen
import com.marvel999.pingmap.ui.wifi.WifiScanScreen
import com.marvel999.pingmap.ui.speed.SpeedTestScreen
import com.marvel999.pingmap.ui.devices.DeviceListScreen
import com.marvel999.pingmap.ui.tools.ToolsScreen

@Composable
fun PingMapNavGraph(
    viewModelFactory: ViewModelProvider.Factory,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            val topLevelRoutes = setOf(
                NavTab.HOME.route,
                NavTab.WIFI.route,
                NavTab.SPEED.route,
                NavTab.DEVICES.route,
                NavTab.TOOLS.route
            )
            if (currentRoute in topLevelRoutes) {
                PingMapBottomNav(currentRoute ?: NavTab.HOME.route) { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavTab.HOME.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavTab.HOME.route) {
                HomeScreen()
            }
            composable(NavTab.WIFI.route) {
                WifiScanScreen()
            }
            composable(NavTab.SPEED.route) {
                SpeedTestScreen()
            }
            composable(NavTab.DEVICES.route) {
                DeviceListScreen()
            }
            composable(NavTab.TOOLS.route) {
                ToolsScreen(onNavigate = { route -> navController.navigate(route) })
            }
        }
    }
}
