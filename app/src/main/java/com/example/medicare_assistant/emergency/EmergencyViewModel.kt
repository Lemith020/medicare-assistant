package com.example.medicare_assistant.emergency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EmergencyViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = EmergencyRepository.get(app)

    val contacts: StateFlow<List<EmergencyContact>> = repo.getContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addContact(name: String, phoneNumber: String) {
        viewModelScope.launch {
            repo.addContact(name, phoneNumber)
        }
    }

    fun deleteContact(id: String) {
        viewModelScope.launch {
            repo.deleteContact(id)
        }
    }
}