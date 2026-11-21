package com.example.medicare_assistant.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.dose.DoseLogRepository
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.Medicine
import com.example.medicare_assistant.medicine.MedicineRepository
import com.example.medicare_assistant.reminder.DoseActions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class Dose(
    val medicineId: Long,
    val slot: DoseSlot,
    val name: String,
    val dosage: String,
    val amountPerDose: String,
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

    private val medicineRepo = MedicineRepository.get(app)
    private val doseLog = DoseLogRepository.get(app)

    val state: StateFlow<HomeState> = combine(medicineRepo.medicines, doseLog.log) { medicines, statuses ->
        val today = LocalDate.now()
        val active = medicines.filter { !it.isCompleted(today) && !it.startDate.isAfter(today) }
        HomeState(
            loaded = true,
            doses = active.flatMap { medicine ->
                medicine.times.map { (slot, time) ->
                    val key = DoseLogRepository.key(today, medicine.id, slot)
                    Dose(medicine.id, slot, medicine.name, medicine.dosage, medicine.amountPerDose, time, statuses[key] ?: DoseStatus.UPCOMING)
                }
            }.sortedBy { it.time },
            lowStock = active.filter { it.isLowStock }.minByOrNull { it.stock },
            completed = medicines.filter { it.isCompleted(today) && !it.completionDismissed }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeState())

    fun mark(dose: Dose, status: DoseStatus) {
        viewModelScope.launch {
            DoseActions.mark(getApplication(), dose.medicineId, dose.slot, status)
        }
    }

    fun dismissCompletion(id: Long) {
        viewModelScope.launch { medicineRepo.dismissCompletion(id) }
    }
}
