package com.example.medicare_assistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
val LocalIsDark = staticCompositionLocalOf { false }

private val LightColors = lightColorScheme(
    primary = LuminousYellow,
    onPrimary = Ink,
    primaryContainer = LuminousYellow,
    onPrimaryContainer = Ink,
    secondary = Ink,
    onSecondary = LuminousYellow,
    secondaryContainer = Color(0xFFFFF1B0),
    onSecondaryContainer = Ink,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color.White,
    onSurfaceVariant = InkMuted,
    surfaceContainerHighest = Color(0xFFF6EFC0),
    outline = DotInactive,
    outlineVariant = CardBorder,
    error = AlertRed
)

private val DarkColors = darkColorScheme(
    primary = LuminousYellow,
    onPrimary = Ink,
    primaryContainer = LuminousYellow,
    onPrimaryContainer = Ink,
    secondary = LuminousYellow,
    onSecondary = Ink,
    secondaryContainer = Color(0xFF4A4210),
    onSecondaryContainer = Color(0xFFFFE680),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkMuted,
    surfaceContainerHighest = Color(0xFF3A3520),
    outline = DarkOutline,
    outlineVariant = DarkCardBorder,
    error = Color(0xFFFFB4AB)
)

@Composable
fun MedicareTheme(
    darkTheme: Boolean,
    textScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current

    // ===== [Text size] phone එකේ font scale එක (density.fontScale) සමඟ ඔයා තෝරපු ප්‍රමාණය (textScale) ගුණ කරනවා. sp වලින් ලියපු හැම text එකක්ම ඒ අනුව ලොකු වෙනවා =====
    CompositionLocalProvider(
        LocalIsDark provides darkTheme,
        LocalDensity provides Density(density.density, density.fontScale * textScale)
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content
        )
    }
}