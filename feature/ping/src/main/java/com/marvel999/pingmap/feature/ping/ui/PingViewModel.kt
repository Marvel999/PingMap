package com.marvel999.pingmap.feature.ping.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.ping.domain.PingRepository
import com.marvel999.pingmap.feature.ping.domain.PingResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PingUiState(
    val host: String = "8.8.8.8",
    val isRunning: Boolean = false,
    val result: PingResult? = null,
    val error: String? = null
)

class PingViewModel @javax.inject.Inject constructor(
    private val pingRepository: PingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PingUiState())
    val state: StateFlow<PingUiState> = _state.asStateFlow()

    fun setHost(host: String) {
        _state.update { it.copy(host = host.trim(), error = null) }
    }

    fun startPing() {
        val host = _state.value.host.ifBlank { "8.8.8.8" }
        if (host.isBlank()) {
            _state.update { it.copy(error = "Enter a hostname or IP") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isRunning = true, error = null, result = null) }
            pingRepository.ping(host, count = 5)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isRunning = false,
                            error = e.message ?: "Ping failed"
                        )
                    }
                }
                .collect { result ->
                    _state.update {
                        it.copy(isRunning = false, result = result)
                    }
                }
        }
    }
}
