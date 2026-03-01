package com.marvel999.pingmap.feature.devices.data

import android.content.Context
import java.util.Locale

/**
 * Looks up manufacturer from MAC OUI prefix.
 * For P2 we use a minimal in-memory map; can be replaced with assets/oui.csv later.
 */
class OuiLookup(context: Context) {
    private val ouiMap: Map<String, String> = buildMap {
        put("00:1A:2B", "Apple Inc.")
        put("3C:A0:67", "Intel")
        put("00:50:56", "VMware")
        put("28:6D:97", "Samsung")
        put("A4:3C:23", "Xiaomi")
        put("DC:A6:32", "Apple")
        put("F0:18:98", "Apple")
        put("00:0C:29", "VMware")
        put("54:E1:AD", "Google")
        put("8C:85:90", "Apple")
    }

    fun lookup(mac: String): String {
        if (mac == "Unknown" || mac.length < 8) return "Unknown"
        val prefix = mac.replace(":", "").take(6).chunked(2).joinToString(":").uppercase(Locale.US)
        return ouiMap.entries.firstOrNull { prefix.startsWith(it.key.replace(":", "").take(6)) }?.value
            ?: ouiMap[mac.uppercase(Locale.US).take(8)]
            ?: "Unknown"
    }
}
