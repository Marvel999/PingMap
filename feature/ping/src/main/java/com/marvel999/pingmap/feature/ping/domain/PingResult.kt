package com.marvel999.pingmap.feature.ping.domain

data class PingResult(
    val host: String,
    val minMs: Long,
    val avgMs: Long,
    val maxMs: Long,
    val jitterMs: Double,
    val packetLoss: Int,
    val packets: List<Long>
)
