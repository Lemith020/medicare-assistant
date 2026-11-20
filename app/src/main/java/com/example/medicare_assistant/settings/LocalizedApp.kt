package com.example.medicare_assistant.settings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun LocalizedApp(languageTag: String, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val localized = remember(languageTag, base) {
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale.forLanguageTag(languageTag))
        base.createConfigurationContext(config)
    }
    CompositionLocalProvider(
        LocalContext provides localized,
        LocalConfiguration provides localized.resources.configuration
    ) {
        content()
    }
}