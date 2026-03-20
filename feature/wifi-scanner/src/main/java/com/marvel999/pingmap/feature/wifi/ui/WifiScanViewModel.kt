package com.marvel999.pingmap.feature.wifi.ui

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.wifi.data.computeChannelCongestion
import com.marvel999.pingmap.feature.wifi.domain.WifiNetwork
import com.marvel999.pingmap.feature.wifi.domain.WifiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WifiScanUiState(
    val networks: List<WifiNetwork> = emptyList(),
    val channelCongestion: Map<Int, Int> = emptyMap(),
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
    /** Mirrors system Wi‑Fi association; "Not connected" if none. */
    val connectedSsid: String = "Not connected"
)

class WifiScanViewModel @Inject constructor(
    private val wifiRepository: WifiRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(WifiScanUiState())
    val state: StateFlow<WifiScanUiState> = _state.asStateFlow()

    init {
        refreshConnectionStatus()
    }

    /** Call when the WiFi tab is shown so status stays current. */
    fun refreshConnectionStatus() {
        _state.update { it.copy(connectedSsid = readConnectedSsid()) }
    }

    private fun readConnectedSsid(): String {
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return try {
            @Suppress("DEPRECATION")
            val info = wm.connectionInfo
            info?.ssid?.removePrefix("\"")?.removeSuffix("\"")?.ifEmpty { null } ?: "Not connected"
        } catch (_: Exception) {
            "Not connected"
        }
    }

    fun startScan() {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, errorMessage = null) }
            try {
                wifiRepository.scan().collect { list ->
                    _state.update {
                        it.copy(
                            networks = list,
                            channelCongestion = computeChannelCongestion(list),
                            isScanning = false,
                            connectedSsid = readConnectedSsid()
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = e.message ?: "Scan failed"
                    )
                }
            }
        }
    }
}
