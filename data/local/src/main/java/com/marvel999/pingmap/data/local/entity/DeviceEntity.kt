package com.marvel999.pingmap.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val macAddress: String,
    val ipAddress: String,
    val hostname: String?,
    val manufacturer: String?,
    val deviceType: String,
    val isCurrentDevice: Boolean,
    val firstSeen: Long,
    val lastSeen: Long
)
