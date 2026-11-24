package com.example.medicare_assistant.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.ui.theme.statusColors
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicineCard(
    medicine: Medicine,
    locale: Locale,
    today: LocalDate,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = statusColors()
    val shape = RoundedCornerShape(24.dp)
    val resources = LocalContext.current.resources
    val completed = medicine.isCompleted(today)
    val low = medicine.isLowStock && !completed

    val isSyrup = medicine.amountPerDose.contains("ml", ignoreCase = true) ||
                  medicine.dosage.contains("ml", ignoreCase = true) ||
                  medicine.dosage.contains("drops", ignoreCase = true)

    val stockText = if (isSyrup) {
        resources.getQuantityString(
            if (low) R.plurals.ml_left_low else R.plurals.ml_left,
            medicine.stock,
            medicine.stock
        )
    } else {
        resources.getQuantityString(
            if (low) R.plurals.tablets_left_low else R.plurals.tablets_left,
            medicine.stock,
            medicine.stock
        )
    }

    val timesText = medicine.times.values.sorted().joinToString(", ") { formatTime(it, locale) }
    val daysLeft = medicine.daysLeft(today)
    val courseText = when {
        medicine.courseDays == null -> stringResource(R.string.course_ongoing)
        completed -> stringResource(R.string.course_completed_on, formatDate(medicine.endDate!!, locale))
        else -> resources.getQuantityString(R.plurals.days_left, daysLeft!!.toInt(), daysLeft.toInt())
    }

    val stockEmoji = if (isSyrup) "💧" else "💊"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LuminousYellow, shape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = medicine.name,
                color = Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            if (medicine.dosage.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val dosageIcon = if (isSyrup) "💧" else "⚖️"
                    Text(text = dosageIcon, fontSize = 14.sp)
                    Text(text = medicine.dosage, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            if (medicine.amountPerDose.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val amountIcon = if (isSyrup) "💧" else "💊"
                    Text(text = amountIcon, fontSize = 14.sp)
                    Text(text = medicine.amountPerDose, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (timesText.isNotBlank()) {
            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.65f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "⏰", fontSize = 14.sp)
                Text(text = timesText, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }

        Row(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.65f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = "📆", fontSize = 14.sp)
            Text(text = courseText, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = stockEmoji, fontSize = 14.sp)
            Text(
                text = stockText,
                color = if (low) colors.badText else Ink,
                fontSize = 16.sp,
                fontWeight = if (low) FontWeight.Bold else FontWeight.Medium
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onEdit,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .semantics { contentDescription = resources.getString(R.string.action_edit_for, medicine.name) },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Ink)
            ) {
                Text(
                    text = stringResource(R.string.action_edit) + " ✏️",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
            Button(
                onClick = onDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .semantics { contentDescription = resources.getString(R.string.action_delete_for, medicine.name) },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFB91C1C))
            ) {
                Text(
                    text = stringResource(R.string.action_delete) + " 🗑️",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
