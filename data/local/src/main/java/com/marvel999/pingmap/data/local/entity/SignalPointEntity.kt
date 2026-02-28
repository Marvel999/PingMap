package com.marvel999.pingmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signal_points")
data class SignalPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val lat: Double,
    val lng: Double,
    val rssi: Int,
    val ssid: String,
    val timestamp: Long
)
