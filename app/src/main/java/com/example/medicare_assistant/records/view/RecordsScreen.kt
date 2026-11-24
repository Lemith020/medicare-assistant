package com.example.medicare_assistant.records.view

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.records.data.MedicalRecord
import com.example.medicare_assistant.records.data.RecordCategory
import com.example.medicare_assistant.records.view.components.RecordCard
import com.example.medicare_assistant.records.view.components.SearchIconButton
import com.example.medicare_assistant.ui.components.AppTextField
import com.example.medicare_assistant.ui.components.ChoiceChip
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow

enum class RecordFilter(@StringRes val labelRes: Int, val category: RecordCategory?) {
    ALL(R.string.records_filter_all, null),
    RECEIPTS(R.string.records_filter_receipts, RecordCategory.RECEIPT),
    REPORTS(R.string.records_filter_reports, RecordCategory.REPORT)
}

data class RecordsUiState(
    val loaded: Boolean = false,
    val records: List<MedicalRecord> = emptyList(),
    val filter: RecordFilter = RecordFilter.ALL,
    val query: String = "",
    val searching: Boolean = false,
    val totalCount: Int = 0
)

data class RecordsActions(
    val onFilterChange: (RecordFilter) -> Unit,
    val onToggleSearch: () -> Unit,
    val onQueryChange: (String) -> Unit,
    val onOpen: (Long) -> Unit,
    val onAdd: () -> Unit
)

@Composable
fun RecordsScreen(state: RecordsUiState, actions: RecordsActions) {
    val locale = LocalConfiguration.current.locales[0]

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.records_title),
                        modifier = Modifier.semantics { heading() },
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.records_subtitle),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
                SearchIconButton(
                    onClick = actions.onToggleSearch,
                    contentDescription = stringResource(R.string.records_search_desc)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecordFilter.entries.forEach { filter ->
                    ChoiceChip(
                        label = stringResource(filter.labelRes),
                        selected = state.filter == filter,
                        onClick = { actions.onFilterChange(filter) },
                        role = Role.RadioButton
                    )
                }
            }

            if (state.searching) {
                Spacer(Modifier.height(12.dp))
                AppTextField(
                    label = stringResource(R.string.records_search_label),
                    value = state.query,
                    onValueChange = actions.onQueryChange,
                    placeholder = stringResource(R.string.records_search_hint)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (state.records.isEmpty()) {
                EmptyRecords(hasAny = state.totalCount > 0, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(state.records, key = { it.id }) { record ->
                        RecordCard(
                            record = record,
                            locale = locale,
                            onClick = { actions.onOpen(record.id) }
                        )
                    }
                }
            }
        }

        Button(
            onClick = actions.onAdd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .heightIn(min = 56.dp)
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LuminousYellow, contentColor = Ink)
        ) {
            Text(
                text = "+ " + stringResource(R.string.records_add),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyRecords(hasAny: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                if (hasAny) R.string.records_nothing_found else R.string.records_empty_title
            ),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
                if (hasAny) R.string.records_nothing_found_body else R.string.records_empty_body
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}