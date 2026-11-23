package com.example.medicare_assistant.profile.logic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.data.Allergy
import com.example.medicare_assistant.profile.data.ProfileRepository
import com.example.medicare_assistant.profile.view.AllergiesUiState
import com.example.medicare_assistant.profile.view.AllergyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private val SUGGESTIONS = listOf(
    "Penicillin",
    "Aspirin",
    "Ibuprofen",
    "Sulfa drugs",
    "Diclofenac",
    "Amoxicillin"
)

class AllergiesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = ProfileRepository.get(app)
    private val input = MutableStateFlow("")
    private val error = MutableStateFlow<Int?>(null)


    val state: StateFlow<AllergiesUiState> = combine(repo.profile, input, error) { profile, text, err ->
        AllergiesUiState(
            loaded = true,
            allergies = profile.allergies.map { AllergyItem(it.name, it.reaction) },
            input = text,
            error = err,
            suggestions = SUGGESTIONS.filter { suggestion ->
                profile.allergies.none { it.name.equals(suggestion, ignoreCase = true) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AllergiesUiState())


    fun onNameChange(value: String) {
        input.value = value.take(40)
        error.value = null
    }


    fun onSuggestionClick(name: String) {
        input.value = name
        error.value = null
    }


    fun add() {
        val name = input.value.trim().replaceFirstChar { it.uppercaseChar() }
        if (name.isEmpty()) {
            error.value = R.string.allergy_error_empty
            return
        }
        viewModelScope.launch {
            val exists = repo.profile.first().allergies.any { it.name.equals(name, ignoreCase = true) }
            if (exists) {
                error.value = R.string.allergy_error_duplicate
                return@launch
            }
            repo.update { it.copy(allergies = it.allergies + Allergy(name)) }
            input.value = ""
            error.value = null
        }
    }


    fun remove(name: String) {
        viewModelScope.launch {
            repo.update { profile ->
                profile.copy(allergies = profile.allergies.filterNot { it.name == name })
            }
        }
    }


    fun saveReaction(name: String, reaction: String) {
        viewModelScope.launch {
            repo.update { profile ->
                profile.copy(
                    allergies = profile.allergies.map {
                        if (it.name == name) it.copy(reaction = reaction.trim()) else it
                    }
                )
            }
        }
    }


    fun clearInput() {
        input.value = ""
        error.value = null
    }
}