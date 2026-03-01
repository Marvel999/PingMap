package com.marvel999.pingmap.feature.ping.domain

import kotlinx.coroutines.flow.Flow

interface PingRepository {
    fun ping(host: String, count: Int): Flow<PingResult>
}
