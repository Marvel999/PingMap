package com.marvel999.pingmap.feature.wifi.domain

/**
 * Result of scoring a WiFi network for the Advisor (Safety, Speed, Crowding).
 * Plain-language labels for UI; badge for recommendation.
 */
data class WiFiScore(
    val total: Int,
    val securityScore: Int,
    val speedScore: Int,
    val congestionScore: Int,
    val badge: Badge,
    val headline: String,
    val securityLabel: String,
    val speedLabel: String,
    val congestionLabel: String
)

enum class Badge {
    BEST_CHOICE,  // 80-100
    GOOD,         // 60-79
    AVERAGE,      // 40-59
    POOR,         // 20-39
    AVOID         // 0-19
}
