package com.example.medicare_assistant.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class OnboardingPrefs(private val context: Context) {

    private val doneKey = booleanPreferencesKey("onboarding_done")

    val isDone: Flow<Boolean> = context.dataStore.data.map { it[doneKey] ?: false }

    suspend fun setDone() {
        context.dataStore.edit { it[doneKey] = true }
    }
}