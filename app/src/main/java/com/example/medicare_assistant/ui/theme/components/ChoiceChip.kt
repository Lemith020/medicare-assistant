package com.example.medicare_assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LocalIsDark
import com.example.medicare_assistant.ui.theme.LuminousYellow

@Composable
fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    role: Role = Role.Checkbox
) {
    val dark = LocalIsDark.current
    val container = when {
        !selected -> Color.Transparent
        dark -> LuminousYellow
        else -> Ink
    }
    val content = when {
        !selected -> MaterialTheme.colorScheme.onSurface
        dark -> Ink
        else -> LuminousYellow
    }
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(container)
            .border(2.dp, MaterialTheme.colorScheme.onSurface, shape)
            .selectable(selected = selected, role = role, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = content,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
