package com.example.medicare_assistant.medicine

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.ui.theme.statusColors
import java.time.LocalDate
import java.util.Locale

@Composable
fun MedicineCard(
    medicine: Medicine,
    locale: Locale,
    today: LocalDate,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = statusColors()
    val shape = RoundedCornerShape(18.dp)
    val resources = LocalContext.current.resources
    val completed = medicine.isCompleted(today)
    val low = medicine.isLowStock && !completed

    val stockText = resources.getQuantityString(
        if (low) R.plurals.tablets_left_low else R.plurals.tablets_left,
        medicine.stock,
        medicine.stock
    )
    val timesText = medicine.times.values.sorted().joinToString(" · ") { formatTime(it, locale) }
    val daysLeft = medicine.daysLeft(today)
    val courseText = when {
        medicine.courseDays == null -> stringResource(R.string.course_ongoing)
        completed -> stringResource(R.string.course_completed_on, formatDate(medicine.endDate!!, locale))
        else -> resources.getQuantityString(R.plurals.days_left, daysLeft!!.toInt(), daysLeft.toInt())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(
                width = if (low) 2.dp else 1.dp,
                color = if (low) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .padding(16.dp)
    ) {
        Text(
            text = "${medicine.name} ${medicine.dosage}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = timesText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        Text(
            text = courseText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stockText,
            color = if (low) colors.badText else MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = if (low) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CardButton(
                text = stringResource(R.string.action_edit),
                description = stringResource(R.string.action_edit_for, medicine.name),
                onClick = onEdit
            )
            CardButton(
                text = stringResource(R.string.action_delete),
                description = stringResource(R.string.action_delete_for, medicine.name),
                onClick = onDelete
            )
        }
    }
}

@Composable
private fun CardButton(text: String, description: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .heightIn(min = 48.dp)
            .semantics { contentDescription = description },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}