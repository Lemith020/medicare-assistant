package com.example.medicare_assistant.medicine

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare_assistant.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

enum class CourseDuration(@StringRes val labelRes: Int, val days: Int?) {
    WEEK_1(R.string.course_1w, 7),
    WEEKS_2(R.string.course_2w, 14),
    WEEKS_3(R.string.course_3w, 21),
    MONTH_1(R.string.course_1m, 30),
    CUSTOM(R.string.course_custom, null),
    ONGOING(R.string.course_ongoing, null)
}

data class AddMedicineState(
    val name: String = "",
    val dosage: String = "",
    val category: MedicineCategory = MedicineCategory.OTHER,
    val times: Map<DoseSlot, LocalTime> = mapOf(
        DoseSlot.MORNING to DoseSlot.MORNING.defaultTime,
        DoseSlot.EVENING to DoseSlot.EVENING.defaultTime
    ),
    val stock: String = "30",
    val threshold: String = "5",
    val startDate: LocalDate = LocalDate.now(),
    val duration: CourseDuration = CourseDuration.WEEKS_2,
    val customDays: String = "",
    @StringRes val nameError: Int? = null,
    @StringRes val slotsError: Int? = null,
    @StringRes val stockError: Int? = null,
    @StringRes val thresholdError: Int? = null,
    @StringRes val daysError: Int? = null
) {
    val courseDays: Int?
        get() = when (duration) {
            CourseDuration.CUSTOM -> customDays.toIntOrNull()
            CourseDuration.ONGOING -> null
            else -> duration.days
        }
}

class AddMedicineViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MedicineRepository.get(app)
    private val _state = MutableStateFlow(AddMedicineState())
    val state: StateFlow<AddMedicineState> = _state.asStateFlow()

    private var started = false
    private var existing: Medicine? = null

    fun start(editId: Long?) {
        if (started) return
        started = true
        if (editId == null) return
        viewModelScope.launch {
            val medicine = repo.medicines.first().firstOrNull { it.id == editId } ?: return@launch
            existing = medicine
            _state.value = medicine.toFormState()
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }

    fun onDosageChange(value: String) = _state.update { it.copy(dosage = value) }

    fun onCategoryChange(value: MedicineCategory) = _state.update { it.copy(category = value) }

    fun toggleSlot(slot: DoseSlot) = _state.update {
        val times = if (slot in it.times) it.times - slot else it.times + (slot to slot.defaultTime)
        it.copy(times = times, slotsError = null)
    }

    fun setTime(slot: DoseSlot, time: LocalTime) = _state.update { it.copy(times = it.times + (slot to time)) }

    fun onStockChange(value: String) =
        _state.update { it.copy(stock = value.filter(Char::isDigit).take(4), stockError = null) }

    fun onThresholdChange(value: String) =
        _state.update { it.copy(threshold = value.filter(Char::isDigit).take(4), thresholdError = null) }

    fun onDurationChange(value: CourseDuration) = _state.update { it.copy(duration = value, daysError = null) }

    fun onCustomDaysChange(value: String) =
        _state.update { it.copy(customDays = value.filter(Char::isDigit).take(3), daysError = null) }

    fun save(onSaved: () -> Unit) {
        val s = _state.value
        val stock = s.stock.toIntOrNull()
        val threshold = s.threshold.toIntOrNull()
        val days = s.courseDays

        val nameError = if (s.name.isBlank()) R.string.error_name else null
        val slotsError = if (s.times.isEmpty()) R.string.error_slots else null
        val stockError = if (stock == null) R.string.error_number else null
        val thresholdError = if (threshold == null) R.string.error_number else null
        val daysError =
            if (s.duration == CourseDuration.CUSTOM && (days == null || days !in 1..365)) R.string.error_days else null

        _state.update {
            it.copy(
                nameError = nameError,
                slotsError = slotsError,
                stockError = stockError,
                thresholdError = thresholdError,
                daysError = daysError
            )
        }

        if (listOf(nameError, slotsError, stockError, thresholdError, daysError).any { it != null }) return

        val old = existing
        val medicine = Medicine(
            id = old?.id ?: System.currentTimeMillis(),
            name = s.name.trim(),
            dosage = s.dosage.trim(),
            category = s.category,
            times = s.times,
            stock = stock!!,
            lowStockThreshold = threshold!!,
            startDate = s.startDate,
            courseDays = days,
            completionDismissed = if (old != null && old.courseDays == days) old.completionDismissed else false
        )

        viewModelScope.launch {
            if (old != null) repo.update(medicine) else repo.add(medicine)
            onSaved()
        }
    }
}

private fun Medicine.toFormState(): AddMedicineState {
    val preset = CourseDuration.entries.firstOrNull { it.days != null && it.days == courseDays }
    val duration = when {
        courseDays == null -> CourseDuration.ONGOING
        preset != null -> preset
        else -> CourseDuration.CUSTOM
    }
    return AddMedicineState(
        name = name,
        dosage = dosage,
        category = category,
        times = times,
        stock = stock.toString(),
        threshold = lowStockThreshold.toString(),
        startDate = startDate,
        duration = duration,
        customDays = if (duration == CourseDuration.CUSTOM) courseDays.toString() else ""
    )
}