package com.marvel999.pingmap.feature.speedtest.domain

data class SpeedTestResult(
    val id: Long = 0,
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Long,
    val jitterMs: Double,
    val packetLoss: Int,
    val serverLocation: String,
    val isp: String,
    val networkType: String,
    val timestamp: Long
)
