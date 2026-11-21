package com.example.medicare_assistant.history

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.Medicine
import java.time.LocalDate
import java.util.Locale

object ReportBuilder {

    private const val LOG_DAYS = 14

    fun build(context: Context, state: HistoryState, today: LocalDate): String {
        val locale = context.resources.configuration.locales[0]
        val from = today.minusDays(state.period.days - 1L)
        val lines = mutableListOf<String>()

        lines += context.getString(R.string.report_title)
        lines += context.getString(
            R.string.report_period,
            formatDate(from, locale),
            formatDate(today, locale),
            state.period.days
        )
        lines += ""

        val percent = state.percent
        if (percent == null) {
            lines += context.getString(R.string.report_no_doses)
        } else {
            lines += context.getString(R.string.report_adherence, percent)
            lines += context.getString(
                R.string.report_counts,
                state.taken,
                state.missed,
                state.skipped,
                state.counted
            )
        }

        if (state.scope.isNotEmpty()) {
            lines += ""
            lines += context.getString(R.string.report_medicines)
            state.scope.forEach { medicine ->
                val times = medicine.times.values.sorted().joinToString(", ") { formatTime(it, locale) }
                val details = "$times · ${courseText(context, medicine, today, locale)}"
                lines += context.getString(R.string.report_med_line, medicine.name, medicine.dosage, details)
            }
        }

        val logDays = state.days.take(LOG_DAYS)
        if (logDays.isNotEmpty()) {
            lines += ""
            lines += context.getString(R.string.report_daily, minOf(state.period.days, LOG_DAYS))
            logDays.forEach { day ->
                lines += formatDate(day.date, locale)
                day.entries.forEach { entry ->
                    lines += "  " + context.getString(
                        R.string.report_entry,
                        formatTime(entry.time, locale),
                        entry.name,
                        entry.dosage,
                        context.getString(statusLabel(entry.status))
                    )
                }
            }
        }

        lines += ""
        lines += context.getString(R.string.report_footer)
        return lines.joinToString("\n")
    }

    private fun statusLabel(status: DoseStatus): Int = when (status) {
        DoseStatus.TAKEN -> R.string.status_taken
        DoseStatus.MISSED -> R.string.status_missed
        DoseStatus.SKIPPED -> R.string.status_skipped
        DoseStatus.UPCOMING -> R.string.status_upcoming
    }

    private fun courseText(context: Context, medicine: Medicine, today: LocalDate, locale: Locale): String {
        val daysLeft = medicine.daysLeft(today)
        return when {
            medicine.courseDays == null -> context.getString(R.string.course_ongoing)
            medicine.isCompleted(today) ->
                context.getString(R.string.course_completed_on, formatDate(medicine.endDate!!, locale))
            else -> context.resources.getQuantityString(R.plurals.days_left, daysLeft!!.toInt(), daysLeft.toInt())
        }
    }
}

fun shareReport(context: Context, text: String, chooserTitle: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val packages = listOf("com.whatsapp", "com.whatsapp.w4b")
    for (pkg in packages) {
        try {
            context.startActivity(Intent(send).setPackage(pkg).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (e: ActivityNotFoundException) {
        }
    }
    context.startActivity(Intent.createChooser(send, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}