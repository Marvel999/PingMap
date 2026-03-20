package com.marvel999.pingmap.di

import android.content.Context
import androidx.room.Room
import com.marvel999.pingmap.data.local.PingMapDatabase
import com.marvel999.pingmap.data.local.dao.DeviceDao
import com.marvel999.pingmap.data.local.dao.ScanSessionDao
import com.marvel999.pingmap.data.local.dao.SignalPointDao
import com.marvel999.pingmap.data.local.dao.SpeedTestDao
import com.marvel999.pingmap.data.local.dao.WifiDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): PingMapDatabase =
        Room.databaseBuilder(context, PingMapDatabase::class.java, "pingmap.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWifiDao(db: PingMapDatabase): WifiDao = db.wifiDao()

    @Provides
    @Singleton
    fun provideSpeedTestDao(db: PingMapDatabase): SpeedTestDao = db.speedTestDao()

    @Provides
    @Singleton
    fun provideDeviceDao(db: PingMapDatabase): DeviceDao = db.deviceDao()

    @Provides
    @Singleton
    fun provideSignalPointDao(db: PingMapDatabase): SignalPointDao = db.signalPointDao()

    @Provides
    @Singleton
    fun provideScanSessionDao(db: PingMapDatabase): ScanSessionDao = db.scanSessionDao()
}
