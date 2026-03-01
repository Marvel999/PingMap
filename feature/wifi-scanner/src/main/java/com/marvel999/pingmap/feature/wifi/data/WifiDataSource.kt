package com.marvel999.pingmap.feature.wifi.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.marvel999.pingmap.feature.wifi.domain.WifiNetwork
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

fun ScanResult.toWifiNetwork(): WifiNetwork = WifiNetwork(
    ssid = SSID.removePrefix("\"").removeSuffix("\"").ifEmpty { "<hidden>" },
    bssid = BSSID ?: "",
    rssi = level,
    frequency = frequency,
    channel = frequencyToChannel(frequency),
    security = parseCapabilities(capabilities),
    signalQuality = rssiToQuality(level),
    distanceMeters = estimateDistance(frequency, level)
)

fun rssiToQuality(rssi: Int): Int = when {
    rssi >= -50 -> 100
    rssi <= -100 -> 0
    else -> 2 * (rssi + 100)
}

fun frequencyToChannel(freq: Int): Int = when {
    freq == 2484 -> 14
    freq in 2412..2472 -> (freq - 2407) / 5
    freq in 5170..5825 -> (freq - 5000) / 5
    else -> -1
}

fun parseCapabilities(capabilities: String?): String {
    if (capabilities.isNullOrEmpty()) return "Open"
    return when {
        capabilities.contains("WPA3") -> "WPA3"
        capabilities.contains("WPA2") -> "WPA2"
        capabilities.contains("WPA") -> "WPA"
        capabilities.contains("WEP") -> "WEP"
        else -> "Open"
    }
}

fun estimateDistance(frequency: Int, rssi: Int): Double {
    val exp = (27.55 - 20 * log10(frequency.toDouble()) + abs(rssi)) / 20.0
    return 10.0.pow(exp)
}

fun computeChannelCongestion(networks: List<WifiNetwork>): Map<Int, Int> =
    networks.filter { it.channel >= 0 }.groupBy { it.channel }.mapValues { it.value.size }
