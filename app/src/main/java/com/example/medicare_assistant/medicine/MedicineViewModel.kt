package com.example.medicare_assistant.medicine

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime

class MedicineViewModel : ViewModel() {

    private val _medicines = MutableStateFlow(
        listOf(
            Medicine(1, "Metformin", "500 mg", listOf(LocalTime.of(14, 0), LocalTime.of(20, 0)), stock = 24),
            Medicine(2, "Amlodipine", "5 mg", listOf(LocalTime.of(8, 0)), stock = 18),
            Medicine(3, "Losartan", "50 mg", listOf(LocalTime.of(13, 0)), stock = 4),
            Medicine(4, "Atorvastatin", "20 mg", listOf(LocalTime.of(21, 0)), stock = 12)
        )
    )
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    fun add(name: String, dosage: String, stock: Int) {
        val newId = (_medicines.value.maxOfOrNull { it.id } ?: 0) + 1
        val newMedicine = Medicine(
            id = newId,
            name = name.ifBlank { "New Medicine" },
            dosage = dosage.ifBlank { "1 pill" },
            times = listOf(LocalTime.of(8, 0)),
            stock = stock
        )
        _medicines.update { it + newMedicine }
    }

    fun update(id: Int, name: String, dosage: String, stock: Int) {
        _medicines.update { list ->
            list.map { med ->
                if (med.id == id) {
                    med.copy(
                        name = name.ifBlank { med.name },
                        dosage = dosage.ifBlank { med.dosage },
                        stock = stock
                    )
                } else {
                    med
                }
            }
        }
    }

    fun delete(id: Int) {
        _medicines.update { list -> list.filterNot { it.id == id } }
    }
}
