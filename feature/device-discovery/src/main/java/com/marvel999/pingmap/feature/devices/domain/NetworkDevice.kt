package com.marvel999.pingmap.feature.devices.domain

data class NetworkDevice(
    val ipAddress: String,
    val macAddress: String,
    val hostname: String?,
    val manufacturer: String?,
    val isCurrentDevice: Boolean = false
)
