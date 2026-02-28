package com.marvel999.pingmap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marvel999.pingmap.data.local.dao.DeviceDao
import com.marvel999.pingmap.data.local.dao.SignalPointDao
import com.marvel999.pingmap.data.local.dao.SpeedTestDao
import com.marvel999.pingmap.data.local.dao.WifiDao
import com.marvel999.pingmap.data.local.entity.DeviceEntity
import com.marvel999.pingmap.data.local.entity.SignalPointEntity
import com.marvel999.pingmap.data.local.entity.SpeedTestEntity
import com.marvel999.pingmap.data.local.entity.WifiScanEntity

@Database(
    entities = [
        WifiScanEntity::class,
        SpeedTestEntity::class,
        DeviceEntity::class,
        SignalPointEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PingMapDatabase : RoomDatabase() {

    abstract fun wifiDao(): WifiDao
    abstract fun speedTestDao(): SpeedTestDao
    abstract fun deviceDao(): DeviceDao
    abstract fun signalPointDao(): SignalPointDao
}
