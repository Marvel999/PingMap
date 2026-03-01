package com.marvel999.pingmap.feature.devices.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.wifi.WifiManager
import android.os.Build
import com.marvel999.pingmap.data.local.dao.DeviceDao
import com.marvel999.pingmap.data.local.entity.DeviceEntity
import com.marvel999.pingmap.feature.devices.domain.DeviceRepository
import com.marvel999.pingmap.feature.devices.domain.NetworkDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Inet4Address
import java.net.InetAddress
import kotlinx.coroutines.coroutineScope

class DeviceRepositoryImpl(
    private val context: Context,
    private val deviceDao: DeviceDao
) : DeviceRepository {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val ouiLookup = OuiLookup(context)

    override fun scan(): Flow<List<NetworkDevice>> = flow {
        val subnet = getSubnet() ?: throw IllegalStateException("Connect to WiFi to discover devices. Network info unavailable.")
        val currentIp = getCurrentWifiIp()
        val devices = discoverDevices(subnet, currentIp)
        deviceDao.insertAll(devices.map { it.toEntity() })
        emit(devices)
    }.flowOn(Dispatchers.IO)

    private fun getSubnet(): String? {
        val network = connectivityManager.activeNetwork ?: return null
        val linkProperties: LinkProperties? = connectivityManager.getLinkProperties(network) ?: return null
        val addr = linkProperties?.linkAddresses?.firstOrNull { it.address is Inet4Address } ?: return null
        val ip = addr.address.hostAddress ?: return null
        return ip.substringBeforeLast(".")
    }

    private fun getCurrentWifiIp(): String? = try {
        if (Build.VERSION.SDK_INT >= 33) {
            connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
                ?.linkAddresses?.firstOrNull { it.address is Inet4Address }?.address?.hostAddress
        } else {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo?.let { info ->
                val ipInt = info.ipAddress
                "${ipInt and 0xff}.${ipInt shr 8 and 0xff}.${ipInt shr 16 and 0xff}.${ipInt shr 24 and 0xff}"
            }
        }
    } catch (_: Exception) {
        null
    }

    private suspend fun discoverDevices(subnet: String, currentIp: String?): List<NetworkDevice> = withContext(Dispatchers.IO) {
        val reachabilityTimeoutMs = 400
        val batchSize = 50
        val hosts = (1..254).toList()
        val results = hosts.chunked(batchSize).flatMap { chunk ->
            coroutineScope {
                chunk.map { host ->
                    async {
                        val ip = "$subnet.$host"
                        try {
                            val address = InetAddress.getByName(ip)
                            if (address.isReachable(reachabilityTimeoutMs)) {
                                val mac = getMacFromArp(ip)
                                val hostname = try {
                                    address.canonicalHostName?.takeIf { it != ip }
                                } catch (_: Exception) {
                                    null
                                }
                                val manufacturer = ouiLookup.lookup(mac)
                                NetworkDevice(
                                    ipAddress = ip,
                                    macAddress = mac,
                                    hostname = hostname,
                                    manufacturer = manufacturer,
                                    isCurrentDevice = ip == currentIp
                                )
                            } else null
                        } catch (_: Exception) {
                            null
                        }
                    }
                }.awaitAll()
            }
        }
        val list = results.filterNotNull().toMutableList()
        if (list.none { it.isCurrentDevice } && currentIp != null) {
            list.add(
                NetworkDevice(
                    ipAddress = currentIp,
                    macAddress = "—",
                    hostname = null,
                    manufacturer = "This device",
                    isCurrentDevice = true
                )
            )
        }
        list.sortedBy { it.ipAddress }
    }

    private fun getMacFromArp(ip: String): String = try {
        File("/proc/net/arp").readLines()
            .drop(1)
            .firstOrNull { it.trimStart().startsWith(ip) }
            ?.split(Regex("\\s+"))
            ?.getOrNull(3) ?: "Unknown"
    } catch (_: Exception) {
        "Unknown"
    }
}

private fun NetworkDevice.toEntity() = DeviceEntity(
    macAddress = macAddress,
    ipAddress = ipAddress,
    hostname = hostname,
    manufacturer = manufacturer,
    deviceType = "UNKNOWN",
    isCurrentDevice = isCurrentDevice,
    firstSeen = System.currentTimeMillis(),
    lastSeen = System.currentTimeMillis()
)
