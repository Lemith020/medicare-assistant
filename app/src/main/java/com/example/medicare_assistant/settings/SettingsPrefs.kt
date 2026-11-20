package com.example.medicare_assistant.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPrefs(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val languageKey = stringPreferencesKey("language")

    val settings: Flow<AppSettings> = context.appDataStore.data.map { prefs ->
        AppSettings(
            themeMode = ThemeMode.entries.firstOrNull { it.name == prefs[themeKey] } ?: ThemeMode.SYSTEM,
            language = AppLanguage.fromTag(prefs[languageKey])
        )
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.appDataStore.edit { it[themeKey] = mode.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.appDataStore.edit { it[languageKey] = language.tag }
    }
}