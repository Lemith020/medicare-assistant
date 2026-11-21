package com.example.medicare_assistant.medicine

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.reminder.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicineViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MedicineRepository.get(app)

    val medicines: StateFlow<List<Medicine>?> =
        repo.medicines.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun delete(id: Long) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), id)
            repo.delete(id)
        }
    }
}