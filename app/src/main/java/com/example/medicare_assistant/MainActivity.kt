package com.example.medicare_assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.medicare_assistant.onboarding.OnboardingScreen
import com.example.medicare_assistant.ui.theme.MedicareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicareTheme {
                OnboardingScreen(onFinished = {})
            }
        }
    }
}