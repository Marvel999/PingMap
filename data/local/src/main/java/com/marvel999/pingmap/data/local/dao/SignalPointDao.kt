package com.marvel999.pingmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel999.pingmap.data.local.entity.SignalPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalPointDao {

    @Query("SELECT * FROM signal_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getBySession(sessionId: String): Flow<List<SignalPointEntity>>

    @Insert
    suspend fun insert(entity: SignalPointEntity)

    @Query("DELETE FROM signal_points WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: String)

    @Query("DELETE FROM signal_points")
    suspend fun deleteAll()
}
