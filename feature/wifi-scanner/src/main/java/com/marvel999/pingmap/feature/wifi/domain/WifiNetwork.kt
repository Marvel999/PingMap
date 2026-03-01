package com.marvel999.pingmap.feature.wifi.domain

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequency: Int,
    val channel: Int,
    val security: String,
    val signalQuality: Int,
    val distanceMeters: Double
)
