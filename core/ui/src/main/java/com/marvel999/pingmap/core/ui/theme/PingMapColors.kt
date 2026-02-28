package com.marvel999.pingmap.core.ui.theme

import androidx.compose.ui.graphics.Color

object PingMapColors {
    val White = Color(0xFFFFFFFF)
    val OffWhite = Color(0xFFF7F9FC)
    val LightGray = Color(0xFFF0F3F7)

    val SoftBlue = Color(0xFF5B8DEF)
    val LightBlue = Color(0xFFE8EFFD)
    val SoftGreen = Color(0xFF4CAF82)
    val SoftAmber = Color(0xFFFFB547)
    val SoftRed = Color(0xFFEF5B5B)
    val LavenderPurple = Color(0xFF9B8DEF)

    val TextPrimary = Color(0xFF1A1D23)
    val TextSecondary = Color(0xFF6B7280)
    val TextHint = Color(0xFFB0B8C4)

    fun signalColor(quality: Int): Color = when {
        quality >= 70 -> SoftGreen
        quality >= 40 -> SoftAmber
        else -> SoftRed
    }
}
