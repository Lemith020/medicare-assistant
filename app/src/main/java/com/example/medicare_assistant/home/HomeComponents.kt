package com.example.medicare_assistant.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.ui.theme.statusColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import com.example.medicare_assistant.dose.DoseStatus

fun formatTime(time: LocalTime, locale: Locale): String =
    time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))

@Composable
fun NextDoseCard(
    dose: Dose,
    locale: Locale,
    onTaken: () -> Unit,
    onSkip: () -> Unit
) {
    val takenDescription = stringResource(R.string.action_taken_for, dose.name)
    val skipDescription = stringResource(R.string.action_skip_for, dose.name)
    val detailsText = if (dose.amountPerDose.isNotBlank()) "${dose.dosage} · ${dose.amountPerDose}" else dose.dosage

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuminousYellow, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Text(
            text = stringResource(R.string.home_next_dose, formatTime(dose.time, locale)),
            color = Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(text = dose.name, color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        if (detailsText.isNotBlank()) {
            Text(text = detailsText, color = Ink, fontSize = 18.sp)
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onTaken,
                modifier = Modifier
                    .weight(2f)
                    .heightIn(min = 56.dp)
                    .semantics { contentDescription = takenDescription },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = LuminousYellow)
            ) {
                Text(stringResource(R.string.action_taken), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 56.dp)
                    .semantics { contentDescription = skipDescription },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Ink),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink)
            ) {
                Text(stringResource(R.string.action_skip), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AllDoneCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuminousYellow, RoundedCornerShape(24.dp))
            .padding(18.dp)
    ) {
        Text(
            text = stringResource(R.string.home_all_done),
            color = Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(text = stringResource(R.string.home_all_done_body), color = Ink, fontSize = 18.sp)
    }
}

@Composable
fun LowStockBanner(name: String, count: Int) {
    val colors = statusColors()
    val shape = RoundedCornerShape(18.dp)
    Text(
        text = stringResource(R.string.home_low_stock, name, count),
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.badBackground, shape)
            .border(1.dp, colors.badBorder, shape)
            .padding(16.dp),
        color = colors.badText,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun DoseRow(dose: Dose, locale: Locale) {
    val shape = RoundedCornerShape(18.dp)
    val detailsText = if (dose.amountPerDose.isNotBlank()) "${dose.dosage} · ${dose.amountPerDose}" else dose.dosage
    val titleText = if (detailsText.isNotBlank()) "${dose.name} $detailsText" else dose.name

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
                text = titleText,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatTime(dose.time, locale),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
        StatusChip(dose.status)
    }
}

@Composable
private fun StatusChip(status: DoseStatus) {
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
