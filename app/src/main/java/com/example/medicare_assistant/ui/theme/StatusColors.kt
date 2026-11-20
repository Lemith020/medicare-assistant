package com.example.medicare_assistant.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class StatusColors(
    val okBackground: Color,
    val okText: Color,
    val badBackground: Color,
    val badText: Color,
    val badBorder: Color,
    val upBackground: Color,
    val upText: Color
)

private val LightStatus = StatusColors(
    okBackground = Color(0xFFDDF3E4),
    okText = Color(0xFF14532D),
    badBackground = Color(0xFFFDE2E0),
    badText = Color(0xFF8C1D18),
    badBorder = Color(0xFFE8A9A4),
    upBackground = Color(0xFFFFF1B0),
    upText = Ink
)

private val DarkStatus = StatusColors(
    okBackground = Color(0xFF1E3A28),
    okText = Color(0xFFA8E6BE),
    badBackground = Color(0xFF3B1B19),
    badText = Color(0xFFFFB4AB),
    badBorder = Color(0xFF7A3B36),
    upBackground = Color(0xFF4A4210),
    upText = Color(0xFFFFE680)
)

@Composable
fun statusColors(): StatusColors = if (LocalIsDark.current) DarkStatus else LightStatus