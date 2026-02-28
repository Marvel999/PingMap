package com.marvel999.pingmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_scans")
data class WifiScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequency: Int,
    val channel: Int,
    val security: String,
    val signalQuality: Int,
    val band: String,
    val distanceMeters: Double,
    val scannedAt: Long
)
