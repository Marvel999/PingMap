package com.marvel999.pingmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvel999.pingmap.data.local.entity.WifiScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WifiDao {

    @Query("SELECT * FROM wifi_scans ORDER BY scannedAt DESC LIMIT :limit")
    fun getRecentScans(limit: Int = 100): Flow<List<WifiScanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<WifiScanEntity>)

    @Query("DELETE FROM wifi_scans WHERE scannedAt < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM wifi_scans")
    suspend fun deleteAll()
}
