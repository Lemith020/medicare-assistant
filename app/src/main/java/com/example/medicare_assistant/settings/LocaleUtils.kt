package com.example.medicare_assistant.settings

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun Context.withLocale(tag: String): Context {
    val config = Configuration(resources.configuration)
    config.setLocale(Locale.forLanguageTag(tag))
    return createConfigurationContext(config)
}