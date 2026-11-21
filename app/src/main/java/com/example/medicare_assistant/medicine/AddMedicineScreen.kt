package com.example.medicare_assistant.medicine

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatDate
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.ui.components.AppTextField
import com.example.medicare_assistant.ui.components.ChoiceChip
import com.example.medicare_assistant.ui.components.PrimaryButton
import java.time.LocalTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddMedicineScreen(
    editId: Long?,
    formKey: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: AddMedicineViewModel = viewModel(key = "add_medicine_$formKey")
    val state by viewModel.state.collectAsState()
    val locale = LocalConfiguration.current.locales[0]
    var pickerSlot by remember { mutableStateOf<DoseSlot?>(null) }

    LaunchedEffect(formKey) { viewModel.start(editId) }

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
            BackPill(onClick = onBack)

            Text(
                text = stringResource(if (editId == null) R.string.form_add_title else R.string.form_edit_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            FormCard {
                AppTextField(
                    label = stringResource(R.string.form_name),
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = stringResource(R.string.form_name_hint),
                    error = state.nameError?.let { stringResource(it) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(14.dp))
                AppTextField(
                    label = stringResource(R.string.form_dosage),
                    value = state.dosage,
                    onValueChange = viewModel::onDosageChange,
                    placeholder = stringResource(R.string.form_dosage_hint),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(Modifier.height(14.dp))
                SectionTitle(stringResource(R.string.form_dose_unit))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DoseUnit.entries.forEach { unit ->
                        ChoiceChip(
                            label = stringResource(unit.labelRes),
                            selected = state.doseUnit == unit,
                            onClick = { viewModel.onDoseUnitChange(unit) },
                            role = Role.RadioButton
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                AppTextField(
                    label = stringResource(R.string.form_amount),
                    value = state.doseQuantity,
                    onValueChange = viewModel::onDoseQuantityChange,
                    placeholder = stringResource(R.string.form_amount_hint),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }

            FormCard {
                SectionTitle(stringResource(R.string.form_category))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MedicineCategory.entries.forEach { category ->
                        ChoiceChip(
                            label = stringResource(category.labelRes),
                            selected = state.category == category,
                            onClick = { viewModel.onCategoryChange(category) },
                            role = Role.RadioButton
                        )
                    }
                }
            }

            FormCard {
                SectionTitle(stringResource(R.string.form_when))
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DoseSlot.entries.forEach { slot ->
                        ChoiceChip(
                            label = stringResource(slot.labelRes),
                            selected = slot in state.times,
                            onClick = { viewModel.toggleSlot(slot) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                state.slotsError?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(it),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DoseSlot.entries.filter { it in state.times }.forEach { slot ->
                        TimeField(
                            label = stringResource(R.string.form_time_for, stringResource(slot.labelRes)),
                            value = formatTime(state.times[slot] ?: slot.defaultTime, locale),
                            onClick = { pickerSlot = slot },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            FormCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(
                        label = stringResource(R.string.form_stock),
                        value = state.stock,
                        onValueChange = viewModel::onStockChange,
                        modifier = Modifier.weight(1f),
                        error = state.stockError?.let { stringResource(it) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                    AppTextField(
                        label = stringResource(R.string.form_alert),
                        value = state.threshold,
                        onValueChange = viewModel::onThresholdChange,
                        modifier = Modifier.weight(1f),
                        error = state.thresholdError?.let { stringResource(it) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            FormCard {
                SectionTitle(stringResource(R.string.form_course))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CourseDuration.entries.forEach { duration ->
                        ChoiceChip(
                            label = stringResource(duration.labelRes),
                            selected = state.duration == duration,
                            onClick = { viewModel.onDurationChange(duration) },
                            role = Role.RadioButton
                        )
                    }
                }
                if (state.duration == CourseDuration.CUSTOM) {
                    Spacer(Modifier.height(12.dp))
                    AppTextField(
                        label = stringResource(R.string.form_custom_days),
                        value = state.customDays,
                        onValueChange = viewModel::onCustomDaysChange,
                        error = state.daysError?.let { stringResource(it) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
                val days = state.courseDays
                val note = when {
                    state.duration == CourseDuration.ONGOING -> stringResource(R.string.form_ongoing_note)
                    days != null && days in 1..365 ->
                        stringResource(R.string.form_ends_on, formatDate(state.startDate.plusDays(days - 1L), locale))
                    else -> null
                }
                if (note != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = note,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            }
        }

        PrimaryButton(
            text = stringResource(R.string.action_save),
            onClick = { viewModel.save(onSaved) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }

    pickerSlot?.let { slot ->
        TimePickerDialog(
            initial = state.times[slot] ?: slot.defaultTime,
            title = stringResource(R.string.form_time_for, stringResource(slot.labelRes)),
            onConfirm = {
                viewModel.setTime(slot, it)
                pickerSlot = null
            },
            onDismiss = { pickerSlot = null }
        )
    }
}

@Composable
private fun BackPill(onClick: () -> Unit) {
    val label = stringResource(R.string.action_back)
    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp)
            .semantics { contentDescription = label },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "←", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
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
private fun TimeField(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
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
                .border(2.dp, MaterialTheme.colorScheme.onSurface, shape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = value, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initial: LocalTime,
    title: String,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val pickerState = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = DateFormat.is24HourFormat(context)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                TimePicker(state = pickerState)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.action_cancel),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    }
                    TextButton(onClick = { onConfirm(LocalTime.of(pickerState.hour, pickerState.minute)) }) {
                        Text(
                            text = stringResource(R.string.action_ok),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}