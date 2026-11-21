package com.example.medicare_assistant.history

import androidx.annotation.StringRes
import com.example.medicare_assistant.R
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.Medicine
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

enum class HistoryPeriod(val days: Int, @StringRes val labelRes: Int) {
    WEEK(7, R.string.history_7_days),
    MONTH(30, R.string.history_30_days),
    ALL(60, R.string.history_all)
}

enum class DayResult { FULL, PARTIAL, MISSED, NONE }

data class HistoryEntry(
    val medicineId: Long,
    val name: String,
    val dosage: String,
    val time: LocalTime,
    val status: DoseStatus
)

data class HistoryDay(
    val date: LocalDate,
    val entries: List<HistoryEntry>
)

data class WeekDay(
    val date: LocalDate,
    val result: DayResult
)

data class HistoryState(
    val loaded: Boolean = false,
    val period: HistoryPeriod = HistoryPeriod.WEEK,
    val medicineFilter: Long? = null,
    val allMedicines: List<Medicine> = emptyList(),
    val scope: List<Medicine> = emptyList(),
    val days: List<HistoryDay> = emptyList(),
    val taken: Int = 0,
    val missed: Int = 0,
    val skipped: Int = 0,
    val streak: Int = 0,
    val week: List<WeekDay> = emptyList()
) {
    val counted: Int get() = taken + missed + skipped

    val percent: Int? get() = if (counted == 0) null else (taken * 100f / counted).roundToInt()
}