package com.example.medicare_assistant.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.ui.theme.StatusColors
import com.example.medicare_assistant.ui.theme.statusColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SummaryCard(state: HistoryState) {
    val percent = state.percent ?: return
    val resources = LocalContext.current.resources

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuminousYellow, RoundedCornerShape(24.dp))
            .padding(18.dp)
            .semantics(mergeDescendants = true) {}
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$percent%", color = Ink, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.history_summary, state.taken, state.counted),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (state.streak > 0) {
                    Text(
                        text = resources.getQuantityString(R.plurals.history_streak, state.streak, state.streak),
                        color = Ink,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Stat(state.taken, R.string.status_taken)
            Stat(state.missed, R.string.status_missed)
            Stat(state.skipped, R.string.status_skipped)
        }
    }
}

@Composable
private fun Stat(count: Int, label: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(text = stringResource(label), color = Ink, fontSize = 14.sp)
    }
}

@Composable
fun WeekStrip(week: List<WeekDay>, locale: Locale) {
    val colors = statusColors()
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.history_week_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            week.forEach { day -> WeekCell(day, locale, colors, Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun WeekCell(day: WeekDay, locale: Locale, colors: StatusColors, modifier: Modifier) {
    val label = day.date.format(DateTimeFormatter.ofPattern("EEE", locale))
    val resultText = stringResource(
        when (day.result) {
            DayResult.FULL -> R.string.day_full
            DayResult.PARTIAL -> R.string.day_partial
            DayResult.MISSED -> R.string.day_missed
            DayResult.NONE -> R.string.day_none
        }
    )
    val symbol = when (day.result) {
        DayResult.FULL -> "✓"
        DayResult.PARTIAL -> "–"
        DayResult.MISSED -> "✕"
        DayResult.NONE -> "·"
    }
    val background = when (day.result) {
        DayResult.FULL -> colors.okBackground
        DayResult.PARTIAL -> colors.upBackground
        DayResult.MISSED -> colors.badBackground
        DayResult.NONE -> Color.Transparent
    }
    val content = when (day.result) {
        DayResult.FULL -> colors.okText
        DayResult.PARTIAL -> colors.upText
        DayResult.MISSED -> colors.badText
        DayResult.NONE -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = "$label: $resultText" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(background, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = symbol, color = content, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DaySection(day: HistoryDay, today: LocalDate, locale: Locale) {
    val title = when (day.date) {
        today -> stringResource(R.string.history_today)
        today.minusDays(1) -> stringResource(R.string.history_yesterday)
        else -> day.date.format(DateTimeFormatter.ofPattern("EEE, d MMM", locale))
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        day.entries.forEach { HistoryRow(it, locale) }
    }
}

@Composable
private fun HistoryRow(entry: HistoryEntry, locale: Locale) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(16.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "${entry.name} ${entry.dosage}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatTime(entry.time, locale),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
        HistoryChip(entry.status)
    }
}

@Composable
private fun HistoryChip(status: DoseStatus) {
    val colors = statusColors()
    val (label, background, text) = when (status) {
        DoseStatus.TAKEN -> Triple(R.string.status_taken, colors.okBackground, colors.okText)
        DoseStatus.MISSED -> Triple(R.string.status_missed, colors.badBackground, colors.badText)
        DoseStatus.SKIPPED -> Triple(R.string.status_skipped, colors.badBackground, colors.badText)
        DoseStatus.UPCOMING -> Triple(R.string.status_upcoming, colors.upBackground, colors.upText)
    }
    Text(
        text = stringResource(label),
        modifier = Modifier
            .background(background, CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}