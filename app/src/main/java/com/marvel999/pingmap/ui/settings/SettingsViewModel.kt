package com.marvel999.pingmap.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marvel999.pingmap.data.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val autoScanOnOpen: Boolean = true,
    val warnUnsafeNetwork: Boolean = true,
    val showExpertDetails: Boolean = false,
    val saveScanHistory: Boolean = true,
    val runSpeedTestAuto: Boolean = false,
    val language: String = "en"
)

class SettingsViewModel @javax.inject.Inject constructor(
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferences.autoScanOnOpen,
                preferences.warnUnsafeNetwork,
                preferences.showExpertDetails
            ) { a, b, c -> Triple(a, b, c) }.combine(
                combine(
                    preferences.saveScanHistory,
                    preferences.runSpeedTestAuto,
                    preferences.language
                ) { d, e, f -> Triple(d, e, f) }
            ) { t1, t2 ->
                SettingsUiState(
                    autoScanOnOpen = t1.first,
                    warnUnsafeNetwork = t1.second,
                    showExpertDetails = t1.third,
                    saveScanHistory = t2.first,
                    runSpeedTestAuto = t2.second,
                    language = t2.third
                )
            }.collect { _state.value = it }
        }
    }

    fun setAutoScanOnOpen(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoScanOnOpen(enabled) }
    }

    fun setWarnUnsafeNetwork(enabled: Boolean) {
        viewModelScope.launch { preferences.setWarnUnsafeNetwork(enabled) }
    }

    fun setShowExpertDetails(enabled: Boolean) {
        viewModelScope.launch { preferences.setShowExpertDetails(enabled) }
    }

    fun setSaveScanHistory(enabled: Boolean) {
        viewModelScope.launch { preferences.setSaveScanHistory(enabled) }
    }

    fun setRunSpeedTestAuto(enabled: Boolean) {
        viewModelScope.launch { preferences.setRunSpeedTestAuto(enabled) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { preferences.setLanguage(code) }
    }
}
