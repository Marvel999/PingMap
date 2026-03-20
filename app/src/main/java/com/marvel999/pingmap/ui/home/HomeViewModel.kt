package com.marvel999.pingmap.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.data.local.dao.DeviceDao
import com.marvel999.pingmap.data.local.dao.ScanSessionDao
import com.marvel999.pingmap.data.local.dao.SpeedTestDao
import com.marvel999.pingmap.data.local.entity.ScanSessionEntity
import com.marvel999.pingmap.data.local.entity.ScanSessionNetworkEntity
import com.marvel999.pingmap.data.preferences.UserPreferencesDataStore
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestRepository
import com.marvel999.pingmap.feature.wifi.domain.ScoringEngine
import com.marvel999.pingmap.feature.wifi.domain.WifiRepository
import com.marvel999.pingmap.service.monitor.MonitorScheduler
import com.marvel999.pingmap.util.UnsafeNetworkNotifier
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
    val notificationDeniedMessage: String? = null,
    val scoredNetworks: List<ScoredNetwork> = emptyList(),
    val isScanning: Boolean = false,
    val detailNetwork: ScoredNetwork? = null,
    val showExpertDetails: Boolean = false,
    val unsafeWarningMessage: String? = null,
    val connectMessage: String? = null,
    /** When false, we do not auto-run a scan on Home open (user setting). Wi‑Fi connection is separate. */
    val autoScanOnOpen: Boolean = true
)

class HomeViewModel @javax.inject.Inject constructor(
    private val speedTestDao: SpeedTestDao,
    private val deviceDao: DeviceDao,
    private val scanSessionDao: ScanSessionDao,
    private val speedTestRepository: SpeedTestRepository,
    private val preferences: UserPreferencesDataStore,
    private val wifiRepository: WifiRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var monitoringRefreshJob: Job? = null

    init {
        viewModelScope.launch {
            preferences.backgroundMonitoringEnabled.collect { enabled ->
                _state.update { it.copy(backgroundMonitoringEnabled = enabled) }
                if (enabled) MonitorScheduler.start(context) else MonitorScheduler.stop(context)
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
            }.collect { new -> _state.update { prev -> new.copy(
                backgroundMonitoringEnabled = prev.backgroundMonitoringEnabled,
                notificationDeniedMessage = prev.notificationDeniedMessage,
                nextRefreshInSeconds = prev.nextRefreshInSeconds,
                scoredNetworks = prev.scoredNetworks,
                isScanning = prev.isScanning,
                detailNetwork = prev.detailNetwork,
                showExpertDetails = prev.showExpertDetails,
                unsafeWarningMessage = prev.unsafeWarningMessage,
                connectMessage = prev.connectMessage,
                autoScanOnOpen = prev.autoScanOnOpen
            ) } }
        }
        viewModelScope.launch {
            preferences.showExpertDetails.collect { value ->
                _state.update { it.copy(showExpertDetails = value) }
            }
        }
        viewModelScope.launch {
            preferences.autoScanOnOpen.collect { enabled ->
                _state.update { it.copy(autoScanOnOpen = enabled) }
            }
        }
    }

    private var hasAutoScannedThisSession = false

    fun onHomeVisible() {
        viewModelScope.launch {
            if (hasAutoScannedThisSession) return@launch
            if (!preferences.autoScanOnOpen.first()) return@launch
            hasAutoScannedThisSession = true
            startScan()
        }
    }

    fun dismissUnsafeWarning() {
        _state.update { it.copy(unsafeWarningMessage = null) }
    }

    fun startScan() {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true) }
            try {
                wifiRepository.scan().collect { list ->
                    val scored = list.map { n -> ScoredNetwork(n, ScoringEngine.score(n, list)) }
                        .sortedByDescending { it.score.total }
                    _state.update { it.copy(scoredNetworks = scored, isScanning = false) }
                    if (preferences.saveScanHistory.first()) saveScanSession(scored)
                    checkUnsafeAndNotify(scored)
                    maybeRunSpeedTestAuto()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isScanning = false) }
            }
        }
    }

    private fun checkUnsafeAndNotify(scored: List<ScoredNetwork>) {
        viewModelScope.launch {
            if (!preferences.warnUnsafeNetwork.first()) return@launch
            val currentSsid = _state.value.ssid
            if (currentSsid == "Not connected") return@launch
            val current = scored.find { it.network.ssid == currentSsid } ?: return@launch
            if (current.score.total < 20) {
                _state.update { it.copy(unsafeWarningMessage = "You're connected to an unsafe network (${current.network.ssid}). Consider switching.") }
                UnsafeNetworkNotifier.showIfNeeded(context, current.network.ssid)
            } else {
                _state.update { it.copy(unsafeWarningMessage = null) }
            }
        }
    }

    private fun maybeRunSpeedTestAuto() {
        viewModelScope.launch {
            if (!preferences.runSpeedTestAuto.first()) return@launch
            val url = preferences.speedTestServerUrl.first()
            speedTestRepository.runTest(url).collect { }
        }
    }

    private suspend fun saveScanSession(scored: List<ScoredNetwork>) {
        if (scored.isEmpty()) return
        val session = ScanSessionEntity(timestamp = System.currentTimeMillis(), locationName = "")
        val sessionId = scanSessionDao.insertSession(session)
        val networks = scored.map { s ->
            ScanSessionNetworkEntity(
                sessionId = sessionId,
                ssid = s.network.ssid,
                bssid = s.network.bssid,
                totalScore = s.score.total,
                badgeText = s.score.badge.name
            )
        }
        scanSessionDao.insertNetworks(networks)
    }

    fun openDetail(scored: ScoredNetwork) {
        _state.update { it.copy(detailNetwork = scored) }
    }

    fun dismissDetail() {
        _state.update { it.copy(detailNetwork = null, connectMessage = null) }
    }

    fun connectToNetwork(scored: ScoredNetwork, password: String? = null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            _state.update { it.copy(connectMessage = "Use Android WiFi settings to connect (Android 10+ required for in-app connect).") }
            return
        }
        viewModelScope.launch {
            try {
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val builder = WifiNetworkSuggestion.Builder().setSsid(scored.network.ssid)
                val isOpen = scored.network.security.equals("Open", ignoreCase = true)
                if (!isOpen && !password.isNullOrBlank()) {
                    builder.setWpa2Passphrase(password)
                }
                val suggestion = builder.build()
                val status = wm.addNetworkSuggestions(listOf(suggestion))
                _state.update {
                    it.copy(
                        connectMessage = when (status) {
                            WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS ->
                                if (isOpen || password != null) "Connect request sent. Android may show a confirmation or prompt for password."
                                else "Secured network — use Android WiFi settings to enter password."
                            else -> "Could not add suggestion. Try connecting from Android WiFi settings."
                        }
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(connectMessage = "Could not add network. Use Android WiFi settings.") }
            }
        }
    }

    fun clearConnectMessage() {
        _state.update { it.copy(connectMessage = null) }
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
                },
                scoredNetworks = prev.scoredNetworks,
                isScanning = prev.isScanning,
                detailNetwork = prev.detailNetwork,
                unsafeWarningMessage = prev.unsafeWarningMessage,
                connectMessage = prev.connectMessage
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
