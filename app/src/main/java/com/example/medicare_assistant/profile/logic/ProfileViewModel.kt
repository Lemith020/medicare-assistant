package com.example.medicare_assistant.profile.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.profile.data.ProfileRepository
import com.example.medicare_assistant.profile.data.UserProfile
import com.example.medicare_assistant.profile.view.ProfileUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProfileRepository.get(app)

    // ===== [Profile data] save කරපු විස්තර UI state එකක් විදියට දෙනවා (Profile page + Home avatar දෙකටම) =====
    val state: StateFlow<ProfileUiState> = repo.profile
        .map { it.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState())
}

private fun UserProfile.toUiState() = ProfileUiState(
    loaded = true,
    isEmpty = isEmpty,
    name = name,
    initial = initial,
    age = age,
    phone = phone,
    address = address,
    locationText = locationText,
    locationUpdatedAt = locationUpdatedAt,
    allergies = allergies.map { it.name }
)