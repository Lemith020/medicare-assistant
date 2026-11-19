package com.example.medicare_assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = LuminousYellow,
    onPrimary = Ink,
    secondary = Ink,
    onSecondary = LuminousYellow,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    error = AlertRed
)

@Composable
fun MedicareTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}