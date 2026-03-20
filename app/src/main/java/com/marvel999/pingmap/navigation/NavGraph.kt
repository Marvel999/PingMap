package com.marvel999.pingmap.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.marvel999.pingmap.core.ui.navigation.NavTab
import com.marvel999.pingmap.core.ui.navigation.PingMapBottomNav
import com.marvel999.pingmap.ui.history.HistoryScreen
import com.marvel999.pingmap.ui.history.HistoryViewModel
import com.marvel999.pingmap.ui.home.HomeScreen
import com.marvel999.pingmap.ui.settings.SettingsScreen
import com.marvel999.pingmap.ui.settings.SettingsViewModel
import com.marvel999.pingmap.ui.home.HomeViewModel
import com.marvel999.pingmap.ui.tools.ToolsScreen
import com.marvel999.pingmap.feature.wifi.ui.WifiScanScreen
import com.marvel999.pingmap.feature.wifi.ui.WifiScanViewModel
import com.marvel999.pingmap.feature.speedtest.ui.SpeedTestScreen
import com.marvel999.pingmap.feature.speedtest.ui.SpeedTestViewModel
import com.marvel999.pingmap.feature.devices.ui.DeviceListScreen
import com.marvel999.pingmap.feature.devices.ui.DeviceDiscoveryViewModel
import com.marvel999.pingmap.feature.ping.ui.PingScreen
import com.marvel999.pingmap.feature.ping.ui.PingViewModel
import com.marvel999.pingmap.feature.portscanner.ui.PortScanScreen
import com.marvel999.pingmap.feature.portscanner.ui.PortScanViewModel
import com.marvel999.pingmap.feature.signalmap.ui.SignalMapScreen
import com.marvel999.pingmap.feature.signalmap.ui.SignalMapViewModel
import androidx.lifecycle.ViewModelProvider

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
                NavTab.SETTINGS.route
            )
            if (currentRoute in topLevelRoutes) {
                PingMapBottomNav(currentRoute ?: NavTab.HOME.route) { tab ->
                    if (tab == NavTab.HOME) {
                        navController.popBackStack(
                            navController.graph.startDestinationId,
                            inclusive = false
                        )
                    } else {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavTab.HOME.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavTab.HOME.route) {
                val vm = viewModel<HomeViewModel>(factory = viewModelFactory)
                HomeScreen(viewModel = vm, onNavigate = { route -> navController.navigate(route) })
            }
            composable(NavTab.WIFI.route) {
                val vm = viewModel<WifiScanViewModel>(factory = viewModelFactory)
                WifiScanScreen(viewModel = vm)
            }
            composable(NavTab.SPEED.route) {
                val vm = viewModel<SpeedTestViewModel>(factory = viewModelFactory)
                SpeedTestScreen(viewModel = vm)
            }
            composable(AppRoutes.HISTORY) {
                val vm = viewModel<HistoryViewModel>(factory = viewModelFactory)
                HistoryScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.DEVICES) {
                val vm = viewModel<DeviceDiscoveryViewModel>(factory = viewModelFactory)
                DeviceListScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.TOOLS) {
                ToolsScreen(
                    onNavigate = { route -> navController.navigate(route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(NavTab.SETTINGS.route) {
                val vm = viewModel<SettingsViewModel>(factory = viewModelFactory)
                SettingsScreen(viewModel = vm)
            }
            composable("ping") {
                val vm = viewModel<PingViewModel>(factory = viewModelFactory)
                PingScreen(viewModel = vm)
            }
            composable("port_scan") {
                val vm = viewModel<PortScanViewModel>(factory = viewModelFactory)
                PortScanScreen(viewModel = vm)
            }
            composable("signal_map") {
                val vm = viewModel<SignalMapViewModel>(factory = viewModelFactory)
                SignalMapScreen(viewModel = vm)
            }
        }
    }
}
