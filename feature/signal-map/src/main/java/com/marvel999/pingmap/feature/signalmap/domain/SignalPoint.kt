package com.marvel999.pingmap.feature.signalmap.domain

data class SignalPoint(
    val id: Long,
    val sessionId: String,
    val lat: Double,
    val lng: Double,
    val rssi: Int,
    val ssid: String,
    val timestamp: Long
)
