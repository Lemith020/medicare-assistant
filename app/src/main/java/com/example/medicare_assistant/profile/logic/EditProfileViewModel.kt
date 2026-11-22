package com.example.medicare_assistant.profile.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.data.ProfileRepository
import com.example.medicare_assistant.profile.view.EditProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProfileRepository.get(app)
    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private var started = false


    fun start() {
        if (started) return
        started = true
        viewModelScope.launch {
            val profile = repo.profile.first()
            _state.value = EditProfileUiState(
                loaded = true,
                name = profile.name,
                age = profile.age?.toString().orEmpty(),
                phone = profile.phone,
                address = profile.address
            )
        }
    }

    // ===== [Full name field] =====
    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }


    fun onAgeChange(value: String) =
        _state.update { it.copy(age = value.filter(Char::isDigit).take(3), ageError = null) }

    // ===== [Phone field] =====
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value.take(20), phoneError = null) }

    // ===== [Address field] =====
    fun onAddressChange(value: String) = _state.update { it.copy(address = value) }


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
            repo.update {
                it.copy(
                    name = s.name.trim(),
                    age = age,
                    phone = phone,
                    address = s.address.trim()
                )
            }
            onSaved()
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        val digits = phone.count { it.isDigit() }
        return phone.all { it.isDigit() || it in "+ ()-" } && digits in 7..15
    }
}