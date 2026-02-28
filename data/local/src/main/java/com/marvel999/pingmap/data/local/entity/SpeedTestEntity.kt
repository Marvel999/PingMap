package com.marvel999.pingmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speed_tests")
data class SpeedTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
