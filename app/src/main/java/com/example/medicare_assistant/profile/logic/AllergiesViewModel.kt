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

    // ===== [Allergy list + suggestions + name field] save කරපු ලැයිස්තුව, ඒකේ නැති suggestions, ටයිප් කරපු නම UI එකට දෙනවා =====
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

    // ===== [Medicine name field] ටයිප් කරන විට error එක අයින් කරනවා =====
    fun onNameChange(value: String) {
        input.value = value.take(40)
        error.value = null
    }

    // ===== [Suggestion chip] ඔබපු නම field එකට පුරවනවා =====
    fun onSuggestionClick(name: String) {
        input.value = name
        error.value = null
    }

    // ===== [Add button] හිස් නම් / දැනටමත් තියෙන නම් error, නැත්නම් ලැයිස්තුවට එකතු කරලා save කරනවා =====
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

    // ===== [Remove button] (තහවුරු කිරීම UI එකේ dialog එකෙන්) ලැයිස්තුවෙන් ඉවත් කරලා save කරනවා =====
    fun remove(name: String) {
        viewModelScope.launch {
            repo.update { profile ->
                profile.copy(allergies = profile.allergies.filterNot { it.name == name })
            }
        }
    }

    // ===== [Allergy card → Reaction dialog: Save] ප්‍රතික්‍රියාව (rash...) save කරනවා =====
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

    // ===== [Page එකෙන් පිටවෙද්දී] ටයිප් කරලා add නොකළ නම ඉවත් කරනවා =====
    fun clearInput() {
        input.value = ""
        error.value = null
    }
}