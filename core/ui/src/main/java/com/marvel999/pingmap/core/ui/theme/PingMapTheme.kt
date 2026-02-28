package com.marvel999.pingmap.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PingMapColors.SoftBlue,
    onPrimary = Color.White,
    secondary = PingMapColors.SoftGreen,
    onSecondary = Color.White,
    background = PingMapColors.White,
    surface = PingMapColors.OffWhite,
    onBackground = PingMapColors.TextPrimary,
    onSurface = PingMapColors.TextPrimary,
    error = PingMapColors.SoftRed,
    onError = Color.White
)

@Composable
fun PingMapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PingMapTypography,
        content = content
    )
}
