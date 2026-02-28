package com.marvel999.pingmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel999.pingmap.data.local.entity.SpeedTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeedTestDao {

    @Query("SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 50): Flow<List<SpeedTestEntity>>

    @Insert
    suspend fun insert(entity: SpeedTestEntity)

    @Query("SELECT * FROM speed_tests ORDER BY timestamp DESC LIMIT 1")
    fun getLatest(): Flow<SpeedTestEntity?>

    @Query("DELETE FROM speed_tests WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("DELETE FROM speed_tests")
    suspend fun deleteAll()
}
