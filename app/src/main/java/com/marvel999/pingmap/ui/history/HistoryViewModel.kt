package com.marvel999.pingmap.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.data.local.dao.ScanSessionDao
import com.marvel999.pingmap.data.local.entity.ScanSessionEntity
import com.marvel999.pingmap.data.local.entity.ScanSessionNetworkEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SessionDisplay(
    val session: ScanSessionEntity,
    val networkCount: Int,
    val topNetwork: ScanSessionNetworkEntity?
)

data class HistoryUiState(
    val sessions: List<SessionDisplay> = emptyList(),
    val detailSession: SessionDisplay? = null,
    val detailSessionNetworks: List<ScanSessionNetworkEntity> = emptyList()
)

class HistoryViewModel @javax.inject.Inject constructor(
    private val scanSessionDao: ScanSessionDao
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            scanSessionDao.getAllSessions().collect { sessions ->
                val withNetworks = sessions.map { session ->
                    val networks = scanSessionDao.getNetworksForSession(session.id).first()
                    SessionDisplay(
                        session = session,
                        networkCount = networks.size,
                        topNetwork = networks.firstOrNull()
                    )
                }
                _state.update { it.copy(sessions = withNetworks) }
            }
        }
    }

    fun openDetail(display: SessionDisplay) {
        viewModelScope.launch {
            val networks = scanSessionDao.getNetworksForSession(display.session.id).first()
            _state.update { it.copy(detailSession = display, detailSessionNetworks = networks) }
        }
    }

    fun dismissDetail() {
        _state.update { it.copy(detailSession = null, detailSessionNetworks = emptyList()) }
    }
}

fun formatSessionDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
