package com.marvel999.pingmap.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * DataStore for user settings. Use default values on read failure (e.g. corruption).
 */
class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val speedTestServerUrl = stringPreferencesKey("speed_test_server_url")
        val lastSignalMapSessionId = stringPreferencesKey("last_signal_map_session_id")
        val backgroundMonitoringEnabled = booleanPreferencesKey("background_monitoring_enabled")
        val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
        val autoScanOnOpen = booleanPreferencesKey("auto_scan_on_open")
        val warnUnsafeNetwork = booleanPreferencesKey("warn_unsafe_network")
        val showExpertDetails = booleanPreferencesKey("show_expert_details")
        val saveScanHistory = booleanPreferencesKey("save_scan_history")
        val runSpeedTestAuto = booleanPreferencesKey("run_speed_test_auto")
        val language = stringPreferencesKey("language")
    }

    val speedTestServerUrl: Flow<String> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs ->
            prefs[Keys.speedTestServerUrl] ?: DEFAULT_SPEED_TEST_URL
        }

    val lastSignalMapSessionId: Flow<String?> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs ->
            prefs[Keys.lastSignalMapSessionId]
        }

    suspend fun setSpeedTestServerUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.speedTestServerUrl] = url
        }
    }

    suspend fun setLastSignalMapSessionId(sessionId: String?) {
        context.dataStore.edit { prefs ->
            if (sessionId != null) {
                prefs[Keys.lastSignalMapSessionId] = sessionId
            } else {
                prefs.remove(Keys.lastSignalMapSessionId)
            }
        }
    }

    val backgroundMonitoringEnabled: Flow<Boolean> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[Keys.backgroundMonitoringEnabled] ?: false }

    suspend fun setBackgroundMonitoringEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.backgroundMonitoringEnabled] = enabled
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences())
            else throw e
        }
        .map { prefs -> prefs[Keys.onboardingCompleted] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.onboardingCompleted] = completed
        }
    }

    val autoScanOnOpen: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.autoScanOnOpen] ?: true }

    suspend fun setAutoScanOnOpen(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.autoScanOnOpen] = enabled }
    }

    val warnUnsafeNetwork: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.warnUnsafeNetwork] ?: true }

    suspend fun setWarnUnsafeNetwork(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.warnUnsafeNetwork] = enabled }
    }

    val showExpertDetails: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.showExpertDetails] ?: false }

    suspend fun setShowExpertDetails(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.showExpertDetails] = enabled }
    }

    val saveScanHistory: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.saveScanHistory] ?: true }

    suspend fun setSaveScanHistory(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.saveScanHistory] = enabled }
    }

    val runSpeedTestAuto: Flow<Boolean> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.runSpeedTestAuto] ?: false }

    suspend fun setRunSpeedTestAuto(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.runSpeedTestAuto] = enabled }
    }

    val language: Flow<String> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs -> prefs[Keys.language] ?: "en" }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { prefs -> prefs[Keys.language] = code }
    }

    companion object {
        const val DEFAULT_SPEED_TEST_URL = "https://speed.cloudflare.com/__down?bytes=25000000"
    }
}
