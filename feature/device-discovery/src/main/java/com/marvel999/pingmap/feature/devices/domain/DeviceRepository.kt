package com.marvel999.pingmap.feature.devices.domain

import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun scan(): Flow<List<NetworkDevice>>
}
