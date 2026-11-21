package com.example.medicare_assistant.history

import com.example.medicare_assistant.dose.DoseLogRepository
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.Medicine
import java.time.LocalDate
import java.time.LocalTime

object HistoryCalculator {

    private const val GRACE_SECONDS = 2 * 60 * 60
    private const val STREAK_DAYS = 60

    fun build(
        medicines: List<Medicine>,
        log: Map<String, DoseStatus>,
        period: HistoryPeriod,
        filter: Long?,
        today: LocalDate,
        now: LocalTime
    ): HistoryState {
        val validFilter = filter?.takeIf { id -> medicines.any { it.id == id } }
        val scope = if (validFilter == null) medicines else medicines.filter { it.id == validFilter }

        fun entriesFor(date: LocalDate): List<HistoryEntry> =
            scope.flatMap { entriesOf(it, date, log, today, now) }.sortedBy { it.time }

        val days = (0 until period.days)
            .map { today.minusDays(it.toLong()) }
            .map { HistoryDay(it, entriesFor(it)) }
            .filter { it.entries.isNotEmpty() }

        val all = days.flatMap { it.entries }

        val week = (6 downTo 0)
            .map { today.minusDays(it.toLong()) }
            .map { WeekDay(it, resultOf(entriesFor(it))) }

        var streak = 0
        for (offset in 0 until STREAK_DAYS) {
            val date = today.minusDays(offset.toLong())
            when (resultOf(entriesFor(date))) {
                DayResult.FULL -> streak++
                DayResult.NONE -> if (offset != 0) break
                else -> break
            }
        }

        return HistoryState(
            loaded = true,
            period = period,
            medicineFilter = validFilter,
            allMedicines = medicines,
            scope = scope,
            days = days,
            taken = all.count { it.status == DoseStatus.TAKEN },
            missed = all.count { it.status == DoseStatus.MISSED },
            skipped = all.count { it.status == DoseStatus.SKIPPED },
            streak = streak,
            week = week
        )
    }

    private fun entriesOf(
        medicine: Medicine,
        date: LocalDate,
        log: Map<String, DoseStatus>,
        today: LocalDate,
        now: LocalTime
    ): List<HistoryEntry> {
        if (date.isBefore(medicine.startDate)) return emptyList()
        val end = medicine.endDate
        if (end != null && date.isAfter(end)) return emptyList()

        return medicine.times.mapNotNull { (slot, time) ->
            val logged = log[DoseLogRepository.key(date, medicine.id, slot)]
            val overdue = now.toSecondOfDay() >= time.toSecondOfDay() + GRACE_SECONDS

            val status: DoseStatus? = when {
                logged == DoseStatus.TAKEN || logged == DoseStatus.SKIPPED -> logged
                date == medicine.startDate ->
                    if (date == today && !overdue) DoseStatus.UPCOMING else null
                date.isBefore(today) -> DoseStatus.MISSED
                overdue -> DoseStatus.MISSED
                else -> DoseStatus.UPCOMING
            }
            status?.let { HistoryEntry(medicine.id, medicine.name, medicine.dosage, time, it) }
        }
    }

    private fun resultOf(entries: List<HistoryEntry>): DayResult {
        val counted = entries.filter { it.status != DoseStatus.UPCOMING }
        if (counted.isEmpty()) return DayResult.NONE
        val taken = counted.count { it.status == DoseStatus.TAKEN }
        return when {
            taken == counted.size -> DayResult.FULL
            taken == 0 -> DayResult.MISSED
            else -> DayResult.PARTIAL
        }
    }
}