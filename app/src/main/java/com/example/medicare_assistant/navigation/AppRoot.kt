package com.example.medicare_assistant.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.onboarding.OnboardingScreen
import com.example.medicare_assistant.onboarding.OnboardingViewModel

@Composable
fun AppRoot(viewModel: OnboardingViewModel = viewModel()) {
    val done by viewModel.isDone.collectAsState(initial = null)

    when (done) {
        null -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        false -> OnboardingScreen(onFinished = {})
        true -> MainScreen()
    }
}