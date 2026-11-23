package com.example.medicare_assistant.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LocalIsDark
import com.example.medicare_assistant.ui.theme.LuminousYellow

@Composable
fun TextSizeCard(
    selected: TextSize,
    onSelect: (TextSize) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(scheme.surface, shape)
            .border(1.dp, scheme.outlineVariant, shape)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.text_size_title),
            modifier = Modifier.semantics { heading() },
            color = scheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.text_size_subtitle),
            color = scheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextSize.entries.forEach { size ->
                SizeTile(
                    size = size,
                    selected = selected == size,
                    onClick = { onSelect(size) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        PreviewBox()
    }
}

@Composable
private fun SizeTile(
    size: TextSize,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val dark = LocalIsDark.current
    val density = LocalDensity.current
    val shape = RoundedCornerShape(14.dp)

    val sampleSize = with(density) {
        when (size) {
            TextSize.NORMAL -> 18.dp
            TextSize.LARGE -> 24.dp
            TextSize.EXTRA_LARGE -> 30.dp
        }.toSp()
    }
    val labelSize = with(density) { 13.dp.toSp() }

    val container = when {
        !selected -> Color.Transparent
        dark -> LuminousYellow
        else -> Ink
    }
    val content = when {
        !selected -> scheme.onSurface
        dark -> Ink
        else -> LuminousYellow
    }

    val label = stringResource(size.labelRes)
    val description = stringResource(R.string.text_size_option_desc, label)

    Column(
        modifier = modifier
            .heightIn(min = 84.dp)
            .clip(shape)
            .background(container)
            .border(2.dp, scheme.onSurface, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .semantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Aa",
            color = content,
            fontSize = sampleSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = content,
            fontSize = labelSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PreviewBox() {
    val scheme = MaterialTheme.colorScheme
    val dashColor = scheme.onSurfaceVariant
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(scheme.background, shape)
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                    )
                )
            }
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(R.string.text_size_preview_label),
            color = scheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Text(
            text = stringResource(R.string.text_size_preview_name),
            color = scheme.onSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.text_size_preview_time),
            color = scheme.onSurface,
            fontSize = 17.sp
        )
    }
}