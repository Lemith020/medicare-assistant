package com.example.medicare_assistant.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = OnboardingPrefs(app)

    val isDone = prefs.isDone

    fun complete(onFinished: () -> Unit) {
        viewModelScope.launch {
            prefs.setDone()
            onFinished()
        }
    }
}