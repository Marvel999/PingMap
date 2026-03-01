package com.marvel999.pingmap.feature.speedtest.domain

import kotlinx.coroutines.flow.Flow

data class SpeedTestProgress(
    val phase: String,
    val currentMbps: Double = 0.0,
    val result: SpeedTestResult? = null
)

interface SpeedTestRepository {
    fun runTest(testUrl: String): Flow<SpeedTestProgress>
    fun getLatestResult(): Flow<SpeedTestResult?>
    fun getHistory(limit: Int): Flow<List<SpeedTestResult>>
}
