package com.marvel999.pingmap.feature.signalmap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.signalmap.domain.SignalMapRepository
import com.marvel999.pingmap.feature.signalmap.domain.SignalPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SignalMapUiState(
    val sessionId: String = UUID.randomUUID().toString(),
    val points: List<SignalPoint> = emptyList(),
    val isRecording: Boolean = false,
    val error: String? = null
)

class SignalMapViewModel @javax.inject.Inject constructor(
    private val signalMapRepository: SignalMapRepository
) : ViewModel() {

    private val sessionIdFlow = MutableStateFlow(UUID.randomUUID().toString())
    private val _state = MutableStateFlow(SignalMapUiState(sessionId = sessionIdFlow.value))
    val state: StateFlow<SignalMapUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionIdFlow.flatMapLatest { id -> signalMapRepository.getPoints(id) }.collect { list ->
                _state.update { it.copy(points = list, sessionId = sessionIdFlow.value) }
            }
        }
    }

    fun startNewSession() {
        val newId = UUID.randomUUID().toString()
        sessionIdFlow.value = newId
        _state.update {
            it.copy(sessionId = newId, points = emptyList(), error = null)
        }
    }

    fun recordPoint() {
        viewModelScope.launch {
            _state.update { it.copy(error = null) }
            signalMapRepository.recordPoint(_state.value.sessionId)
                .onSuccess {
                    // Points will update via Flow
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message ?: "Failed to record point") }
                }
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            signalMapRepository.clearSession(sessionIdFlow.value)
            _state.update { it.copy(points = emptyList()) }
        }
    }
}
