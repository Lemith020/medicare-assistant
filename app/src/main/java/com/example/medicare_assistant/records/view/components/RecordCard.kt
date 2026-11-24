package com.example.medicare_assistant.records.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.records.data.MedicalRecord
import java.util.Locale

@Composable
fun RecordCard(
    record: MedicalRecord,
    locale: Locale,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(16.dp)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            CategoryTag(record.category)
            Spacer(Modifier.height(6.dp))
            Text(
                text = record.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = record.source,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
            Text(
                text = formatDate(record.date, locale),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
        Spacer(Modifier.width(12.dp))
        if (record.imagePath != null) {
            RecordImage(
                path = record.imagePath,
                maxSidePx = 200,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
            )
        } else {
            DocumentIcon()
        }
    }
}

@Composable
private fun DocumentIcon() {
    val line = MaterialTheme.colorScheme.onSurface
    val background = MaterialTheme.colorScheme.surfaceContainerHighest

    Canvas(Modifier.size(56.dp)) {
        drawCircle(background)
        val w = size.width
        val h = size.height
        drawRoundRect(
            color = line,
            topLeft = Offset(w * 0.34f, h * 0.28f),
            size = Size(w * 0.32f, h * 0.44f),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(2.dp.toPx())
        )
        listOf(0.42f, 0.52f, 0.62f).forEach { y ->
            drawLine(
                color = line,
                start = Offset(w * 0.40f, h * y),
                end = Offset(w * 0.60f, h * y),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}