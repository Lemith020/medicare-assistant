package com.example.medicare_assistant.profile.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.medicare_assistant.ui.components.ChoiceChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.forEach { name ->
            ChoiceChip(
                label = name,
                selected = false,
                onClick = { onClick(name) },
                role = Role.Button
            )
        }
    }
}