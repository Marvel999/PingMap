package com.marvel999.pingmap.feature.wifi.domain

/**
 * Scores a WiFi network for the Advisor: Safety (40%), Speed (35%), Congestion (25%).
 * Pure logic, no Android dependency.
 */
object ScoringEngine {

    fun score(network: WifiNetwork, allNetworks: List<WifiNetwork>): WiFiScore {
        val security = scoreSecurity(network.security)
        val speed = scoreSpeed(network.rssi, network.frequency)
        val congestion = scoreCongestion(network.channel, allNetworks)
        val total = (security * 0.40 + speed * 0.35 + congestion * 0.25).toInt().coerceIn(0, 100)
        val badge = toBadge(total)
        return WiFiScore(
            total = total,
            securityScore = security,
            speedScore = speed,
            congestionScore = congestion,
            badge = badge,
            headline = buildHeadline(security, speed, congestion),
            securityLabel = securityLabel(security),
            speedLabel = speedLabel(speed),
            congestionLabel = congestionLabel(congestion)
        )
    }

    private fun scoreSecurity(security: String): Int = when {
        security.contains("WPA3") -> 100
        security.contains("WPA2") && security.contains("Enterprise") -> 90
        security.contains("WPA2") -> 70
        security.contains("WPA") && security.contains("WPA2") -> 50
        security.contains("WPA") -> 30
        security.contains("WEP") -> 10
        else -> 0  // Open
    }

    private fun scoreSpeed(rssi: Int, frequency: Int): Int {
        val is5Ghz = frequency in 5170..5825
        return when {
            is5Ghz && rssi > -55 -> 100
            is5Ghz && rssi in -67..-55 -> 80
            is5Ghz && rssi in -75..-67 -> 60
            !is5Ghz && rssi > -60 -> 55
            !is5Ghz && rssi in -70..-60 -> 35
            rssi < -75 -> 10
            else -> 50
        }
    }

    private fun scoreCongestion(channel: Int, allNetworks: List<WifiNetwork>): Int {
        if (channel < 0) return 50
        val onSameChannel = allNetworks.count { it.channel == channel }
        return when (onSameChannel) {
            1 -> 100
            2 -> 80
            3 -> 60
            4 -> 40
            else -> 10
        }
    }

    private fun toBadge(total: Int): Badge = when {
        total >= 80 -> Badge.BEST_CHOICE
        total >= 60 -> Badge.GOOD
        total >= 40 -> Badge.AVERAGE
        total >= 20 -> Badge.POOR
        else -> Badge.AVOID
    }

    private fun buildHeadline(security: Int, speed: Int, congestion: Int): String {
        val parts = mutableListOf<String>()
        if (security >= 70) parts.add("Safe")
        else if (security < 40) parts.add("Unsafe")
        if (speed >= 60) parts.add("Fast")
        else if (speed < 40) parts.add("Slow")
        if (congestion >= 60) parts.add("Uncrowded")
        else if (congestion < 40) parts.add("Crowded")
        return parts.take(3).joinToString(", ").ifEmpty { "Check details" }
    }

    private fun securityLabel(score: Int): String = when {
        score >= 90 -> "Very Secure"
        score >= 70 -> "Secure"
        score >= 50 -> "Okay"
        score >= 30 -> "Weak"
        score >= 10 -> "Dangerous"
        else -> "Unsafe"
    }

    private fun speedLabel(score: Int): String = when {
        score >= 80 -> "Fast"
        score >= 60 -> "Okay"
        score >= 40 -> "Moderate"
        score >= 20 -> "Slow"
        else -> "Very Slow"
    }

    private fun congestionLabel(score: Int): String = when {
        score >= 80 -> "Uncrowded"
        score >= 60 -> "Some competition"
        score >= 40 -> "Crowded"
        else -> "Very crowded"
    }
}
