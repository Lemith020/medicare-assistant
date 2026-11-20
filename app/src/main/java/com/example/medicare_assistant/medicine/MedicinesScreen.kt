package com.example.medicare_assistant.medicine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.ui.components.PrimaryButton
import java.time.LocalDate

@Composable
fun MedicinesScreen(
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: MedicineViewModel = viewModel()
) {
    val loaded by viewModel.medicines.collectAsState()
    val medicines = loaded ?: return
    val locale = LocalConfiguration.current.locales[0]
    val resources = LocalContext.current.resources
    val today = remember { LocalDate.now() }
    var pendingDelete by remember { mutableStateOf<Medicine?>(null) }

    val active = medicines.filter { !it.isCompleted(today) }
    val completed = medicines.filter { it.isCompleted(today) }
    val groups = MedicineCategory.entries.mapNotNull { category ->
        active.filter { it.category == category }.takeIf { it.isNotEmpty() }?.let { category to it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.medicines_title),
            modifier = Modifier.semantics { heading() },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = resources.getQuantityString(R.plurals.medicines_count, medicines.size, medicines.size),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))

        if (medicines.isEmpty()) {
            EmptyState(Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                groups.forEach { (category, list) ->
                    item(key = "header_${category.name}") {
                        SectionHeader(title = stringResource(category.labelRes), count = list.size)
                    }
                    items(list, key = { it.id }) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            locale = locale,
                            today = today,
                            onEdit = { onEditClick(medicine.id) },
                            onDelete = { pendingDelete = medicine }
                        )
                    }
                }
                if (completed.isNotEmpty()) {
                    item(key = "header_completed") {
                        SectionHeader(title = stringResource(R.string.section_completed), count = completed.size)
                    }
                    items(completed, key = { it.id }) { medicine ->
                        MedicineCard(
                            medicine = medicine,
                            locale = locale,
                            today = today,
                            onEdit = { onEditClick(medicine.id) },
                            onDelete = { pendingDelete = medicine }
                        )
                    }
                }
            }
        }

        PrimaryButton(
            text = stringResource(R.string.action_add_medicine),
            onClick = onAddClick,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }

    pendingDelete?.let { medicine ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.dialog_delete_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.dialog_delete_message, medicine.name), fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(medicine.id)
                    pendingDelete = null
                }) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
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

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .semantics { heading() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = count.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.medicines_empty_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.medicines_empty_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
    }
}