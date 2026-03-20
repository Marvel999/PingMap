package com.marvel999.pingmap.feature.speedtest.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestProgress
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestRepository
import com.marvel999.pingmap.feature.speedtest.domain.SpeedTestResult
import com.marvel999.pingmap.data.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TestPhase { IDLE, PINGING, DOWNLOADING, UPLOADING, DONE }

data class SpeedTestUiState(
    val isRunning: Boolean = false,
    val phase: TestPhase = TestPhase.IDLE,
    val currentSpeedMbps: Double = 0.0,
    val liveDownloadMbps: Double = 0.0,
    val liveUploadMbps: Double = 0.0,
    val result: SpeedTestResult? = null,
    val history: List<SpeedTestResult> = emptyList(),
    val error: String? = null,
    val detailResult: SpeedTestResult? = null
)

class SpeedTestViewModel @javax.inject.Inject constructor(
    private val speedTestRepository: SpeedTestRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SpeedTestUiState())
    val state: StateFlow<SpeedTestUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            speedTestRepository.getHistory(10).collect { list ->
                _state.update { it.copy(history = list) }
            }
        }
    }

    fun startTest() {
        viewModelScope.launch {
            val url = preferences.speedTestServerUrl.first()
            _state.update {
                it.copy(
                    isRunning = true,
                    phase = TestPhase.PINGING,
                    error = null,
                    result = null,
                    currentSpeedMbps = 0.0,
                    liveDownloadMbps = 0.0,
                    liveUploadMbps = 0.0
                )
            }
            speedTestRepository.runTest(url)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isRunning = false,
                            phase = TestPhase.IDLE,
                            error = e.message ?: "Test failed"
                        )
                    }
                }
                .collect { progress ->
                    val phase = when (progress.phase) {
                        "Ping" -> TestPhase.PINGING
                        "Download" -> TestPhase.DOWNLOADING
                        "Upload" -> TestPhase.UPLOADING
                        "Done" -> TestPhase.DONE
                        else -> _state.value.phase
                    }
                    _state.update {
                        val next = it.copy(
                            phase = phase,
                            currentSpeedMbps = progress.currentMbps,
                            isRunning = phase != TestPhase.DONE
                        )
                        when (phase) {
                            TestPhase.DOWNLOADING -> next.copy(liveDownloadMbps = progress.currentMbps)
                            TestPhase.UPLOADING -> next.copy(liveUploadMbps = progress.currentMbps)
                            TestPhase.DONE -> next.copy(
                                liveDownloadMbps = progress.result?.downloadMbps ?: it.liveDownloadMbps,
                                liveUploadMbps = progress.result?.uploadMbps ?: it.liveUploadMbps
                            )
                            else -> next
                        }
                    }
                    if (phase == TestPhase.DONE) {
                        val latest = progress.result ?: speedTestRepository.getLatestResult().first()
                        latest?.let { _state.update { it.copy(result = latest) } }
                        speedTestRepository.getHistory(10).first().let { list ->
                            _state.update { it.copy(history = list) }
                        }
                    }
                }
        }
    }

    fun openDetail(result: SpeedTestResult) {
        _state.update { it.copy(detailResult = result) }
    }

    fun dismissDetail() {
        _state.update { it.copy(detailResult = null) }
    }
}
