package com.marvel999.pingmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.marvel999.pingmap.data.local.entity.ScanSessionEntity
import com.marvel999.pingmap.data.local.entity.ScanSessionNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanSessionDao {

    @Insert
    suspend fun insertSession(session: ScanSessionEntity): Long

    @Insert
    suspend fun insertNetworks(networks: List<ScanSessionNetworkEntity>)

    @Query("SELECT * FROM scan_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ScanSessionEntity>>

    @Query("SELECT * FROM scan_session_networks WHERE sessionId = :sessionId ORDER BY totalScore DESC")
    fun getNetworksForSession(sessionId: Long): Flow<List<ScanSessionNetworkEntity>>
}
