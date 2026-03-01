package com.marvel999.pingmap.feature.wifi.domain

import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    fun scan(): Flow<List<WifiNetwork>>
}
