package com.marvel999.pingmap.feature.portscanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.portscanner.domain.PortResult
import com.marvel999.pingmap.feature.portscanner.domain.PortScannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PortScanUiState(
    val host: String = "",
    val isScanning: Boolean = false,
    val openPorts: List<PortResult> = emptyList(),
    val error: String? = null
)

class PortScanViewModel @javax.inject.Inject constructor(
    private val portScannerRepository: PortScannerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PortScanUiState())
    val state: StateFlow<PortScanUiState> = _state.asStateFlow()

    fun setHost(host: String) {
        _state.update { it.copy(host = host.trim(), error = null) }
    }

    fun startScan() {
        val host = _state.value.host.ifBlank { null } ?: run {
            _state.update { it.copy(error = "Enter a hostname or IP") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isScanning = true, error = null, openPorts = emptyList()) }
            val openList = mutableListOf<PortResult>()
            portScannerRepository.scan(host, 1..1024, timeoutMs = 300)
                .catch { e ->
                    _state.update {
                        it.copy(isScanning = false, error = e.message ?: "Scan failed")
                    }
                }
                .collect { result ->
                    if (result.isOpen) {
                        openList.add(result)
                        _state.update { it.copy(openPorts = openList.toList()) }
                    }
                }
            _state.update { it.copy(isScanning = false) }
        }
    }
}
