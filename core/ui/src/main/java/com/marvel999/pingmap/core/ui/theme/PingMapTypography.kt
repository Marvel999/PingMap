package com.marvel999.pingmap.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PingMapTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 62.sp,
        fontWeight = FontWeight.Bold,
        color = PingMapColors.TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = PingMapColors.TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 17.sp,
        fontWeight = FontWeight.Medium,
        color = PingMapColors.TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp,
        color = PingMapColors.TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        color = PingMapColors.TextHint
    )
)
