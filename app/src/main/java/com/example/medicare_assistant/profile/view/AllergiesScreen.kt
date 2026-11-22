package com.example.medicare_assistant.profile.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.view.components.AllergyInputRow
import com.example.medicare_assistant.profile.view.components.AllergyListItem
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.profile.view.components.SuggestionChips
import com.example.medicare_assistant.ui.components.AllergyWarningCard
import com.example.medicare_assistant.ui.components.AppTextField

data class AllergyItem(
    val name: String,
    val reaction: String
)

data class AllergiesUiState(
    val loaded: Boolean = false,
    val allergies: List<AllergyItem> = emptyList(),
    val input: String = "",
    @StringRes val error: Int? = null,
    val suggestions: List<String> = emptyList()
)

data class AllergiesActions(
    val onBack: () -> Unit,
    val onNameChange: (String) -> Unit,
    val onAdd: () -> Unit,
    val onSuggestionClick: (String) -> Unit,
    val onRemove: (String) -> Unit,
    val onSaveReaction: (name: String, reaction: String) -> Unit
)

@Composable
fun AllergiesScreen(state: AllergiesUiState, actions: AllergiesActions) {
    var removing by remember { mutableStateOf<AllergyItem?>(null) }
    var editing by remember { mutableStateOf<AllergyItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PageTopBar(onBack = actions.onBack)

            Text(
                text = stringResource(R.string.profile_allergies),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.allergy_subtitle),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )

            AllergyInputRow(
                value = state.input,
                onValueChange = actions.onNameChange,
                error = state.error,
                onAdd = actions.onAdd
            )

            if (state.suggestions.isNotEmpty()) {
                SuggestionChips(
                    suggestions = state.suggestions,
                    onClick = actions.onSuggestionClick
                )
            }

            if (state.allergies.isEmpty()) {
                Text(
                    text = stringResource(R.string.profile_no_allergies),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            } else {
                state.allergies.forEach { item ->
                    AllergyListItem(
                        name = item.name,
                        reaction = item.reaction,
                        onClick = { editing = item },
                        onRemove = { removing = item }
                    )
                }

                Text(
                    text = stringResource(R.string.allergy_preview_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                AllergyWarningCard(medicineName = state.allergies.first().name)
            }
        }
    }

    removing?.let { item ->
        AlertDialog(
            onDismissRequest = { removing = null },
            title = { Text(stringResource(R.string.allergy_remove_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.allergy_remove_message, item.name), fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = {
                    actions.onRemove(item.name)
                    removing = null
                }) {
                    Text(
                        text = stringResource(R.string.action_remove),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { removing = null }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    editing?.let { item ->
        var text by remember(item.name) { mutableStateOf(item.reaction) }

        AlertDialog(
            onDismissRequest = { editing = null },
            title = {
                Text(
                    text = stringResource(R.string.allergy_reaction_title, item.name),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                AppTextField(
                    label = stringResource(R.string.allergy_reaction_label),
                    value = text,
                    onValueChange = { text = it.take(60) },
                    placeholder = stringResource(R.string.allergy_reaction_hint)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    actions.onSaveReaction(item.name, text)
                    editing = null
                }) {
                    Text(
                        text = stringResource(R.string.action_save_short),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { editing = null }) {
                    Text(
                        text = stringResource(R.string.action_cancel),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}