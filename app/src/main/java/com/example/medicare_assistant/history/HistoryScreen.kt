package com.example.medicare_assistant.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.settings.AppLanguage
import com.example.medicare_assistant.settings.withLocale
import com.example.medicare_assistant.ui.components.ChoiceChip
import com.example.medicare_assistant.ui.components.PrimaryButton
import java.time.LocalDate

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    if (state.loaded) {
        HistoryContent(state = state, viewModel = viewModel)
    }
}

@Composable
private fun HistoryContent(state: HistoryState, viewModel: HistoryViewModel) {
    val context = LocalContext.current
    val locale = LocalConfiguration.current.locales[0]
    val today = remember { LocalDate.now() }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.history_title),
                    modifier = Modifier.semantics { heading() },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HistoryPeriod.entries.forEach { period ->
                        ChoiceChip(
                            label = stringResource(period.labelRes),
                            selected = state.period == period,
                            onClick = { viewModel.setPeriod(period) },
                            modifier = Modifier.weight(1f),
                            role = Role.RadioButton
                        )
                    }
                }
            }

            if (state.allMedicines.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            ChoiceChip(
                                label = stringResource(R.string.history_filter_all),
                                selected = state.medicineFilter == null,
                                onClick = { viewModel.setFilter(null) },
                                role = Role.RadioButton
                            )
                        }
                        items(state.allMedicines, key = { it.id }) { medicine ->
                            ChoiceChip(
                                label = medicine.name,
                                selected = state.medicineFilter == medicine.id,
                                onClick = { viewModel.setFilter(medicine.id) },
                                role = Role.RadioButton
                            )
                        }
                    }
                }
            }

            if (state.days.isEmpty()) {
                item { EmptyHistory() }
            } else {
                item { SummaryCard(state) }
                item { WeekStrip(state.week, locale) }
                items(state.days, key = { it.date.toString() }) { day ->
                    DaySection(day = day, today = today, locale = locale)
                }
            }
        }

        if (state.days.isNotEmpty()) {
            PrimaryButton(
                text = stringResource(R.string.action_share_report),
                onClick = { showLanguageDialog = true },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }

    if (showLanguageDialog) {
        ReportLanguageDialog(
            onPick = { language ->
                showLanguageDialog = false
                val localized = context.withLocale(language.tag)
                val text = ReportBuilder.build(localized, state, LocalDate.now())
                shareReport(context, text, localized.getString(R.string.action_share_report))
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
private fun EmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.history_empty_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.history_empty_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReportLanguageDialog(onPick: (AppLanguage) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(R.string.share_dialog_title), fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(stringResource(R.string.share_dialog_body), fontSize = 16.sp)
                AppLanguage.entries.forEach { language ->
                    ChoiceChip(
                        label = language.nativeName,
                        selected = false,
                        onClick = { onPick(language) },
                        modifier = Modifier.fillMaxWidth(),
                        role = Role.Button
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
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