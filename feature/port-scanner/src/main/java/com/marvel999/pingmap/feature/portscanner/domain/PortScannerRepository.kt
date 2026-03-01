package com.marvel999.pingmap.feature.portscanner.domain

import kotlinx.coroutines.flow.Flow

interface PortScannerRepository {
    fun scan(host: String, portRange: IntRange, timeoutMs: Int): Flow<PortResult>
}
