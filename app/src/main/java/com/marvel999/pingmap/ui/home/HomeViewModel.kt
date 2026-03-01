package com.marvel999.pingmap.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.data.local.dao.DeviceDao
import com.marvel999.pingmap.data.local.dao.SpeedTestDao
import com.marvel999.pingmap.data.preferences.UserPreferencesDataStore
import com.marvel999.pingmap.service.monitor.MonitorScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val ssid: String = "Not connected",
    val signalQuality: Int = 0,
    val band: String = "",
    val security: String = "",
    val localIp: String = "-",
    val lastDownloadMbps: Double? = null,
    val lastUploadMbps: Double? = null,
    val lastSpeedTestTimestamp: Long? = null,
    val nextRefreshInSeconds: Int? = null,
    val deviceCount: Int = 0,
    val networkStatusMessage: String = "Checking...",
    val backgroundMonitoringEnabled: Boolean = false,
    val notificationDeniedMessage: String? = null
)

class HomeViewModel @javax.inject.Inject constructor(
    private val speedTestDao: SpeedTestDao,
    private val deviceDao: DeviceDao,
    private val preferences: UserPreferencesDataStore,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var monitoringRefreshJob: Job? = null

    init {
        viewModelScope.launch {
            preferences.backgroundMonitoringEnabled.collect { enabled ->
                _state.update { it.copy(backgroundMonitoringEnabled = enabled) }
                if (enabled) MonitorScheduler.start(context)
                monitoringRefreshJob?.cancel()
                monitoringRefreshJob = null
                if (enabled) {
                    monitoringRefreshJob = viewModelScope.launch {
                        refreshWifiInfo()
                        while (true) {
                            _state.update { it.copy(nextRefreshInSeconds = 2) }
                            delay(1_000)
                            _state.update { it.copy(nextRefreshInSeconds = 1) }
                            delay(1_000)
                            refreshWifiInfo()
                        }
                    }
                } else {
                    _state.update { it.copy(nextRefreshInSeconds = null) }
                }
            }
        }
        viewModelScope.launch {
            combine(
                speedTestDao.getLatest(),
                deviceDao.getAll()
            ) { latestSpeed, devices ->
                val wifiInfo = getCurrentWifiInfo()
                HomeUiState(
                    ssid = wifiInfo.ssid,
                    signalQuality = wifiInfo.quality,
                    band = wifiInfo.band,
                    security = wifiInfo.security,
                    localIp = wifiInfo.localIp,
                    lastDownloadMbps = latestSpeed?.downloadMbps,
                    lastUploadMbps = latestSpeed?.uploadMbps,
                    lastSpeedTestTimestamp = latestSpeed?.timestamp,
                    deviceCount = devices.size,
                    networkStatusMessage = when {
                        wifiInfo.ssid == "Not connected" -> "Connect to Wi‑Fi to see details"
                        wifiInfo.quality >= 70 -> "Your network looks good"
                        wifiInfo.quality >= 40 -> "Fair signal — try moving closer to the router"
                        else -> "Weak signal"
                    }
                )
            }.collect { new -> _state.update { prev -> new.copy(backgroundMonitoringEnabled = prev.backgroundMonitoringEnabled, notificationDeniedMessage = prev.notificationDeniedMessage, nextRefreshInSeconds = prev.nextRefreshInSeconds) } }
        }
    }

    fun startMonitoring() {
        viewModelScope.launch {
            MonitorScheduler.start(context)
            preferences.setBackgroundMonitoringEnabled(true)
            _state.update { it.copy(notificationDeniedMessage = null) }
        }
    }

    fun stopMonitoring() {
        viewModelScope.launch {
            MonitorScheduler.stop(context)
            preferences.setBackgroundMonitoringEnabled(false)
        }
    }

    fun setNotificationDeniedMessage(message: String?) {
        _state.update { it.copy(notificationDeniedMessage = message) }
    }

    private suspend fun refreshWifiInfo() {
        val wifi = getCurrentWifiInfo()
        val latestSpeed = speedTestDao.getLatest().first()
        _state.update { prev ->
            prev.copy(
                ssid = wifi.ssid,
                signalQuality = wifi.quality,
                band = wifi.band,
                security = wifi.security,
                localIp = wifi.localIp,
                lastDownloadMbps = latestSpeed?.downloadMbps,
                lastUploadMbps = latestSpeed?.uploadMbps,
                lastSpeedTestTimestamp = latestSpeed?.timestamp,
                networkStatusMessage = when {
                    wifi.ssid == "Not connected" -> "Connect to Wi‑Fi to see details"
                    wifi.quality >= 70 -> "Your network looks good"
                    wifi.quality >= 40 -> "Fair signal — try moving closer to the router"
                    else -> "Weak signal"
                }
            )
        }
    }

    private fun getCurrentWifiInfo(): WifiInfoResult {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return try {
            @Suppress("DEPRECATION")
            val info = wm.connectionInfo
            val ssid = info?.ssid?.removePrefix("\"")?.removeSuffix("\"")?.ifEmpty { null } ?: "Not connected"
            val rssi = info?.rssi ?: -100
            val quality = when {
                rssi >= -50 -> 100
                rssi <= -100 -> 0
                else -> 2 * (rssi + 100)
            }
            val freq = info?.frequency ?: 0
            val band = when {
                freq in 2412..2484 -> "2.4 GHz"
                freq in 5170..5825 -> "5 GHz"
                else -> ""
            }
            val ipInt = info?.ipAddress ?: 0
            val localIp = if (ipInt != 0) "${ipInt and 0xff}.${ipInt shr 8 and 0xff}.${ipInt shr 16 and 0xff}.${ipInt shr 24 and 0xff}" else "-"
            WifiInfoResult(ssid, quality, band, "WPA2", localIp)
        } catch (_: Exception) {
            WifiInfoResult("Not connected", 0, "", "", "-")
        }
    }

    private data class WifiInfoResult(
        val ssid: String,
        val quality: Int,
        val band: String,
        val security: String,
        val localIp: String
    )
}
