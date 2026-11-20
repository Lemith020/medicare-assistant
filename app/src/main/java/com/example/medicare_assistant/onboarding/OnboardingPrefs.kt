package com.example.medicare_assistant.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.medicare_assistant.core.appDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OnboardingPrefs(private val context: Context) {

    private val doneKey = booleanPreferencesKey("onboarding_done")

    val isDone: Flow<Boolean> = context.appDataStore.data.map { it[doneKey] ?: false }

    suspend fun setDone() {
        context.appDataStore.edit { it[doneKey] = true }
    }
}