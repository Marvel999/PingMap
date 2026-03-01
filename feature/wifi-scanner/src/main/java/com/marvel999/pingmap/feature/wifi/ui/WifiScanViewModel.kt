package com.marvel999.pingmap.feature.wifi.ui

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

data class WifiScanUiState(
    val networks: List<WifiNetwork> = emptyList(),
    val channelCongestion: Map<Int, Int> = emptyMap(),
    val isScanning: Boolean = false,
    val errorMessage: String? = null
)

class WifiScanViewModel @javax.inject.Inject constructor(
    private val wifiRepository: WifiRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WifiScanUiState())
    val state: StateFlow<WifiScanUiState> = _state.asStateFlow()

    fun startScan() {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, errorMessage = null) }
            try {
                wifiRepository.scan().collect { list ->
                    _state.update {
                        it.copy(
                            networks = list,
                            channelCongestion = computeChannelCongestion(list),
                            isScanning = false
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
