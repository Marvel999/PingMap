package com.marvel999.pingmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marvel999.pingmap.data.local.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY lastSeen DESC")
    fun getAll(): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<DeviceEntity>)

    @Query("SELECT * FROM devices WHERE macAddress = :mac LIMIT 1")
    suspend fun getByMac(mac: String): DeviceEntity?

    @Query("DELETE FROM devices")
    suspend fun deleteAll()
}
