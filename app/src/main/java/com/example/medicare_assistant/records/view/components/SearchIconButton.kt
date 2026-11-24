package com.example.medicare_assistant.records.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SearchIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val color = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(26.dp)) {
            val stroke = 2.5.dp.toPx()
            drawCircle(
                color = color,
                radius = size.width * 0.3f,
                center = Offset(size.width * 0.42f, size.height * 0.42f),
                style = Stroke(stroke)
            )
            drawLine(
                color = color,
                start = Offset(size.width * 0.64f, size.height * 0.64f),
                end = Offset(size.width * 0.9f, size.height * 0.9f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}