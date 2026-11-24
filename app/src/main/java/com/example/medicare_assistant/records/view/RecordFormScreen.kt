package com.example.medicare_assistant.records.view

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.records.data.RecordCategory
import com.example.medicare_assistant.records.view.components.NotesField
import com.example.medicare_assistant.records.view.components.RecordImage
import com.example.medicare_assistant.ui.components.AppTextField
import com.example.medicare_assistant.ui.components.ChoiceChip
import com.example.medicare_assistant.ui.components.PrimaryButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

data class RecordFormUiState(
    val loaded: Boolean = false,
    val editing: Boolean = false,
    val category: RecordCategory = RecordCategory.REPORT,
    val title: String = "",
    val source: String = "",
    val date: LocalDate = LocalDate.now(),
    val notes: String = "",
    val imagePath: String? = null,
    val saving: Boolean = false,
    @StringRes val titleError: Int? = null,
    @StringRes val sourceError: Int? = null,
    @StringRes val dateError: Int? = null
)

data class RecordFormActions(
    val onBack: () -> Unit,
    val onCategoryChange: (RecordCategory) -> Unit,
    val onTitleChange: (String) -> Unit,
    val onSourceChange: (String) -> Unit,
    val onDateChange: (LocalDate) -> Unit,
    val onNotesChange: (String) -> Unit,
    val onTakePhoto: () -> Unit,
    val onChoosePhoto: () -> Unit,
    val onRemovePhoto: () -> Unit,
    val onSave: () -> Unit
)

@Composable
fun RecordFormScreen(state: RecordFormUiState, actions: RecordFormActions) {
    val locale = LocalConfiguration.current.locales[0]
    var showDatePicker by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            PageTopBar(onBack = actions.onBack)

            Text(
                text = stringResource(
                    if (state.editing) R.string.record_form_edit_title else R.string.record_form_add_title
                ),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            FormCard {
                SectionTitle(stringResource(R.string.record_form_type))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecordCategory.entries.forEach { category ->
                        ChoiceChip(
                            label = stringResource(category.labelRes),
                            selected = state.category == category,
                            onClick = { actions.onCategoryChange(category) },
                            role = Role.RadioButton
                        )
                    }
                }
            }

            FormCard {
                AppTextField(
                    label = stringResource(R.string.record_form_title),
                    value = state.title,
                    onValueChange = actions.onTitleChange,
                    placeholder = stringResource(R.string.record_form_title_hint),
                    error = state.titleError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(14.dp))
                AppTextField(
                    label = stringResource(R.string.record_form_source),
                    value = state.source,
                    onValueChange = actions.onSourceChange,
                    placeholder = stringResource(R.string.record_form_source_hint),
                    error = state.sourceError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(14.dp))
                DateField(
                    label = stringResource(R.string.record_form_date),
                    value = formatDate(state.date, locale),
                    error = state.dateError?.let { stringResource(it) },
                    onClick = { showDatePicker = true }
                )
                Spacer(Modifier.height(14.dp))
                NotesField(
                    label = stringResource(R.string.record_form_notes),
                    value = state.notes,
                    onValueChange = actions.onNotesChange,
                    placeholder = stringResource(R.string.record_form_notes_hint)
                )
            }

            FormCard {
                SectionTitle(stringResource(R.string.record_form_photo))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhotoButton(
                        text = stringResource(R.string.record_form_take_photo),
                        onClick = actions.onTakePhoto,
                        modifier = Modifier.weight(1f)
                    )
                    PhotoButton(
                        text = stringResource(R.string.record_form_choose_photo),
                        onClick = actions.onChoosePhoto,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (state.imagePath != null) {
                    Spacer(Modifier.height(12.dp))
                    RecordImage(
                        path = state.imagePath,
                        maxSidePx = 900,
                        contentDescription = stringResource(R.string.record_photo_preview_desc),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = actions.onRemovePhoto) {
                        Text(
                            text = stringResource(R.string.record_form_remove_photo),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        PrimaryButton(
            text = stringResource(R.string.record_form_save),
            onClick = actions.onSave,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }

    if (showDatePicker) {
        RecordDatePicker(
            initial = state.date,
            onConfirm = {
                actions.onDateChange(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(bottom = 10.dp),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun DateField(
    label: String,
    value: String,
    error: String?,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {}
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
                .border(2.dp, if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface, shape)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
        }
        if (error != null) {
            Text(
                text = error,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun PhotoButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordDatePicker(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = pickerState.selectedDateMillis
                if (millis != null) {
                    onConfirm(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                } else {
                    onDismiss()
                }
            }) {
                Text(
                    text = stringResource(R.string.action_ok),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }
        },
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        DatePicker(state = pickerState)
    }
}