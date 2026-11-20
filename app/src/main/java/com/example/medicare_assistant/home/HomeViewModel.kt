package com.example.medicare_assistant.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.medicine.Medicine
import com.example.medicare_assistant.medicine.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

enum class DoseStatus { TAKEN, MISSED, SKIPPED, UPCOMING }

data class Dose(
    val key: String,
    val name: String,
    val dosage: String,
    val time: LocalTime,
    val status: DoseStatus
)

data class HomeState(
    val loaded: Boolean = false,
    val doses: List<Dose> = emptyList(),
    val lowStock: Medicine? = null,
    val completed: List<Medicine> = emptyList()
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MedicineRepository.get(app)
    private val marks = MutableStateFlow<Map<String, DoseStatus>>(emptyMap())

    val state: StateFlow<HomeState> = combine(repo.medicines, marks) { medicines, statuses ->
        val today = LocalDate.now()
        val active = medicines.filter { !it.isCompleted(today) && !it.startDate.isAfter(today) }
        HomeState(
            loaded = true,
            doses = active.flatMap { medicine ->
                medicine.times.values.map { time ->
                    val key = "$today|${medicine.id}|$time"
                    Dose(key, medicine.name, medicine.dosage, time, statuses[key] ?: DoseStatus.UPCOMING)
                }
            }.sortedBy { it.time },
            lowStock = active.filter { it.isLowStock }.minByOrNull { it.stock },
            completed = medicines.filter { it.isCompleted(today) && !it.completionDismissed }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState())

    fun mark(key: String, status: DoseStatus) {
        marks.update { it + (key to status) }
    }

    fun dismissCompletion(id: Long) {
        viewModelScope.launch { repo.dismissCompletion(id) }
    }
}