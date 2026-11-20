package com.example.medicare_assistant.medicine

import androidx.annotation.StringRes
import com.example.medicare_assistant.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

enum class MedicineCategory(@StringRes val labelRes: Int) {
    DIABETES(R.string.cat_diabetes),
    BLOOD_PRESSURE(R.string.cat_blood_pressure),
    CHOLESTEROL(R.string.cat_cholesterol),
    HEART(R.string.cat_heart),
    INFECTION(R.string.cat_infection),
    COLD_FEVER(R.string.cat_cold_fever),
    PAIN(R.string.cat_pain),
    STOMACH(R.string.cat_stomach),
    VITAMINS(R.string.cat_vitamins),
    OTHER(R.string.cat_other)
}

enum class DoseSlot(@StringRes val labelRes: Int, val defaultTime: LocalTime) {
    MORNING(R.string.slot_morning, LocalTime.of(8, 0)),
    NOON(R.string.slot_noon, LocalTime.of(13, 0)),
    EVENING(R.string.slot_evening, LocalTime.of(20, 0)),
    NIGHT(R.string.slot_night, LocalTime.of(22, 0))
}

data class Medicine(
    val id: Long,
    val name: String,
    val dosage: String,
    val category: MedicineCategory,
    val times: Map<DoseSlot, LocalTime>,
    val stock: Int,
    val lowStockThreshold: Int,
    val startDate: LocalDate,
    val courseDays: Int?,
    val completionDismissed: Boolean = false
) {
    val isLowStock: Boolean get() = stock <= lowStockThreshold

    val endDate: LocalDate? get() = courseDays?.let { startDate.plusDays(it - 1L) }

    fun isCompleted(today: LocalDate): Boolean = endDate?.isBefore(today) ?: false

    fun daysLeft(today: LocalDate): Long? = endDate?.let { ChronoUnit.DAYS.between(today, it) + 1 }
}