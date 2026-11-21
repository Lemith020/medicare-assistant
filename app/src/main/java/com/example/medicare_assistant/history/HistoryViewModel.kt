package com.example.medicare_assistant.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.dose.DoseLogRepository
import com.example.medicare_assistant.medicine.MedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime

class HistoryViewModel(app: Application) : AndroidViewModel(app) {

    private val medicineRepo = MedicineRepository.get(app)
    private val doseLog = DoseLogRepository.get(app)

    private val period = MutableStateFlow(HistoryPeriod.WEEK)
    private val filter = MutableStateFlow<Long?>(null)
    private val tick = MutableStateFlow(0)

    val state: StateFlow<HistoryState> = combine(
        medicineRepo.medicines,
        doseLog.log,
        period,
        filter,
        tick
    ) { medicines, log, selectedPeriod, selectedFilter, _ ->
        HistoryCalculator.build(medicines, log, selectedPeriod, selectedFilter, LocalDate.now(), LocalTime.now())
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HistoryState())

    fun setPeriod(value: HistoryPeriod) {
        period.value = value
    }

    fun setFilter(medicineId: Long?) {
        filter.value = medicineId
    }

    fun refresh() {
        tick.value += 1
    }
}