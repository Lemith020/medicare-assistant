package com.example.medicare_assistant.profile.logic

import android.app.Application
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.data.ProfileRepository
import com.example.medicare_assistant.profile.view.EditProfileUiState
import com.example.medicare_assistant.settings.SettingsPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class EditProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProfileRepository.get(app)
    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private var started = false
    private var savedPhotoPath: String? = null
    private var pendingPhotoPath: String? = null
    private var pendingLocation: LocationResult.Success? = null


    fun start() {
        if (started) return
        started = true
        viewModelScope.launch {
            val profile = repo.profile.first()
            savedPhotoPath = profile.photoPath
            _state.value = EditProfileUiState(
                loaded = true,
                name = profile.name,
                age = profile.age?.toString().orEmpty(),
                phone = profile.phone,
                address = profile.address,
                photoPath = profile.photoPath,
                locationText = profile.locationText,
                locationUpdatedAt = profile.locationUpdatedAt
            )
        }
    }


    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }


    fun onAgeChange(value: String) =
        _state.update { it.copy(age = value.filter(Char::isDigit).take(3), ageError = null) }


    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value.take(20), phoneError = null) }


    fun onAddressChange(value: String) = _state.update { it.copy(address = value) }


    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            val path = PhotoHelper.importToPending(getApplication<Application>(), uri)
            if (path != null) {
                pendingPhotoPath = path
                _state.update { it.copy(photoPath = path) }
            }
        }
    }


    fun useCurrentLocation() {
        if (_state.value.locating) return
        viewModelScope.launch {
            _state.update { it.copy(locating = true, locationMessage = null) }
            val app = getApplication<Application>()
            val locale = Locale.forLanguageTag(SettingsPrefs(app).settings.first().language.tag)

            when (val result = LocationHelper.fetch(app, locale)) {
                is LocationResult.Success -> {
                    pendingLocation = result
                    _state.update {
                        it.copy(
                            locating = false,
                            locationText = result.text,
                            locationUpdatedAt = System.currentTimeMillis()
                        )
                    }
                }
                LocationResult.PermissionDenied -> locationFailed(R.string.location_error_permission)
                LocationResult.Disabled -> locationFailed(R.string.location_error_disabled)
                LocationResult.Failed -> locationFailed(R.string.location_error_failed)
            }
        }
    }

    // ===== [Permission popup: Don't allow] =====
    fun onLocationPermissionDenied() = locationFailed(R.string.location_error_permission)

    private fun locationFailed(@StringRes message: Int) {
        _state.update { it.copy(locating = false, locationMessage = message) }
    }


    fun save(onSaved: () -> Unit) {
        val s = _state.value
        val age = s.age.toIntOrNull()
        val phone = s.phone.trim()

        val nameError = if (s.name.isBlank()) R.string.edit_error_name else null
        val ageError = if (s.age.isNotEmpty() && (age == null || age !in 1..120)) R.string.edit_error_age else null
        val phoneError = if (phone.isNotEmpty() && !isValidPhone(phone)) R.string.edit_error_phone else null

        _state.update { it.copy(nameError = nameError, ageError = ageError, phoneError = phoneError) }

        if (listOf(nameError, ageError, phoneError).any { it != null }) return

        viewModelScope.launch {
            val pending = pendingPhotoPath
            val photo = if (pending != null) {
                PhotoHelper.commit(getApplication<Application>(), pending, savedPhotoPath)
            } else {
                savedPhotoPath
            }
            val location = pendingLocation

            repo.update {
                var updated = it.copy(
                    name = s.name.trim(),
                    age = age,
                    phone = phone,
                    address = s.address.trim(),
                    photoPath = photo
                )
                if (location != null) {
                    updated = updated.copy(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        locationText = location.text,
                        locationUpdatedAt = s.locationUpdatedAt ?: System.currentTimeMillis()
                    )
                }
                updated
            }
            onSaved()
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val digits = phone.count { it.isDigit() }
        return phone.all { it.isDigit() || it in "+ ()-" } && digits in 7..15
    }
}