package com.example.medicare_assistant.profile.logic

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.data.ProfileRepository
import com.example.medicare_assistant.profile.data.UserProfile
import com.example.medicare_assistant.profile.view.ProfileUiState
import com.example.medicare_assistant.settings.SettingsPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class LocationUi(
    val locating: Boolean = false,
    @StringRes val message: Int? = null
)

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProfileRepository.get(app)
    private val locationUi = MutableStateFlow(LocationUi())


    val state: StateFlow<ProfileUiState> = combine(repo.profile, locationUi) { profile, location ->
        profile.toUiState(location)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState())


    fun refreshLocation() {
        if (locationUi.value.locating) return
        viewModelScope.launch {
            locationUi.value = LocationUi(locating = true)
            val app = getApplication<Application>()
            val locale = Locale.forLanguageTag(SettingsPrefs(app).settings.first().language.tag)

            locationUi.value = when (val result = LocationHelper.fetch(app, locale)) {
                is LocationResult.Success -> {
                    repo.update {
                        it.copy(
                            latitude = result.latitude,
                            longitude = result.longitude,
                            locationText = result.text,
                            locationUpdatedAt = System.currentTimeMillis()
                        )
                    }
                    LocationUi()
                }
                LocationResult.PermissionDenied -> LocationUi(message = R.string.location_error_permission)
                LocationResult.Disabled -> LocationUi(message = R.string.location_error_disabled)
                LocationResult.Failed -> LocationUi(message = R.string.location_error_failed)
            }
        }
    }


    fun onLocationPermissionDenied() {
        locationUi.value = LocationUi(message = R.string.location_error_permission)
    }
}

private fun UserProfile.toUiState(location: LocationUi) = ProfileUiState(
    loaded = true,
    isEmpty = isEmpty,
    name = name,
    initial = initial,
    age = age,
    phone = phone,
    address = address,
    locationText = locationText,
    locationUpdatedAt = locationUpdatedAt,
    allergies = allergies.map { it.name },
    photoPath = photoPath,
    locating = location.locating,
    locationMessage = location.message
)