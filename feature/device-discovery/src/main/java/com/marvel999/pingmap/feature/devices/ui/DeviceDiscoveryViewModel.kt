package com.marvel999.pingmap.feature.devices.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.devices.domain.DeviceRepository
import com.marvel999.pingmap.feature.devices.domain.NetworkDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceListUiState(
    val devices: List<NetworkDevice> = emptyList(),
    val subnet: String = "",
    val isScanning: Boolean = false,
    val error: String? = null
)

class DeviceDiscoveryViewModel @javax.inject.Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DeviceListUiState())
    val state: StateFlow<DeviceListUiState> = _state.asStateFlow()

    fun startScan() {
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, error = null) }
            try {
                deviceRepository.scan().collect { list ->
                    _state.update {
                        it.copy(
                            devices = list,
                            subnet = list.firstOrNull()?.ipAddress?.substringBeforeLast(".")?.plus(".0/24") ?: "",
                            isScanning = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isScanning = false, error = e.message ?: "Scan failed")
                }
            }
        }
    }
}
