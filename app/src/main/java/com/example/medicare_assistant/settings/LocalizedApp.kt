package com.example.medicare_assistant.settings

import android.content.res.Configuration
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun LocalizedApp(languageTag: String, content: @Composable () -> Unit) {
    val base = LocalContext.current
    val registryOwner = LocalActivityResultRegistryOwner.current
    val localized = remember(languageTag, base) {
        val config = Configuration(base.resources.configuration)
        config.setLocale(Locale.forLanguageTag(languageTag))
        base.createConfigurationContext(config)
    }

    val providers = buildList {
        add(LocalContext provides localized)
        add(LocalConfiguration provides localized.resources.configuration)
        if (registryOwner != null) {
            add(LocalActivityResultRegistryOwner provides registryOwner)
        }
    }

    CompositionLocalProvider(*providers.toTypedArray()) {
        content()
    }
}
