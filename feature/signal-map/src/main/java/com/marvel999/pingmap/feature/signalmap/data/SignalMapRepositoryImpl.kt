package com.marvel999.pingmap.feature.signalmap.data

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import com.marvel999.pingmap.data.local.dao.SignalPointDao
import com.marvel999.pingmap.data.local.entity.SignalPointEntity
import com.marvel999.pingmap.feature.signalmap.domain.SignalMapRepository
import com.marvel999.pingmap.feature.signalmap.domain.SignalPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SignalMapRepositoryImpl(
    private val context: Context,
    private val signalPointDao: SignalPointDao
) : SignalMapRepository {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override fun getPoints(sessionId: String): Flow<List<SignalPoint>> =
        signalPointDao.getBySession(sessionId).map { list -> list.map { it.toDomain() } }

    override suspend fun recordPoint(sessionId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val location = try {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (_: Exception) {
            null
        }
        if (location == null) {
            return@withContext Result.failure(Exception("Location unavailable. Enable location and try again."))
        }
        val rssi = try {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo?.rssi ?: -100
        } catch (_: Exception) {
            -100
        }
        val ssid = try {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo?.ssid?.removeSurrounding("\"") ?: ""
        } catch (_: Exception) {
            ""
        }
        signalPointDao.insert(
            SignalPointEntity(
                sessionId = sessionId,
                lat = location.latitude,
                lng = location.longitude,
                rssi = rssi,
                ssid = ssid,
                timestamp = System.currentTimeMillis()
            )
        )
        Result.success(Unit)
    }

    override suspend fun clearSession(sessionId: String) = withContext(Dispatchers.IO) {
        signalPointDao.deleteBySession(sessionId)
    }
}

private fun SignalPointEntity.toDomain() = SignalPoint(
    id = id,
    sessionId = sessionId,
    lat = lat,
    lng = lng,
    rssi = rssi,
    ssid = ssid,
    timestamp = timestamp
)
