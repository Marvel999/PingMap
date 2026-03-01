package com.marvel999.pingmap.feature.signalmap.domain

import kotlinx.coroutines.flow.Flow

interface SignalMapRepository {
    fun getPoints(sessionId: String): Flow<List<SignalPoint>>
    suspend fun recordPoint(sessionId: String): Result<Unit>
    suspend fun clearSession(sessionId: String)
}
