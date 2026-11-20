package com.example.medicare_assistant.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime

enum class DoseStatus { TAKEN, MISSED, SKIPPED, UPCOMING }

data class Dose(
    val id: Int,
    val name: String,
    val dosage: String,
    val time: LocalTime,
    val status: DoseStatus
)

class HomeViewModel : ViewModel() {

    private val _doses = MutableStateFlow(
        listOf(
            Dose(1, "Amlodipine", "5 mg", LocalTime.of(8, 0), DoseStatus.TAKEN),
            Dose(2, "Losartan", "50 mg", LocalTime.of(13, 0), DoseStatus.MISSED),
            Dose(3, "Metformin", "500 mg", LocalTime.of(14, 0), DoseStatus.UPCOMING),
            Dose(4, "Atorvastatin", "20 mg", LocalTime.of(21, 0), DoseStatus.UPCOMING)
        )
    )
    val doses: StateFlow<List<Dose>> = _doses.asStateFlow()

    val lowStockName = "Losartan"
    val lowStockCount = 4

    fun mark(id: Int, status: DoseStatus) {
        _doses.update { list -> list.map { if (it.id == id) it.copy(status = status) else it } }
    }
}