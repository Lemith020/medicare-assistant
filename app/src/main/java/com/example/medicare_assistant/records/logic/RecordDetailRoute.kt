package com.example.medicare_assistant.records.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.records.view.RecordDetailActions
import com.example.medicare_assistant.records.view.RecordDetailScreen

@Composable
fun RecordDetailRoute(
    recordId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val viewModel: RecordDetailViewModel = viewModel(key = "record_detail_$recordId")
    val state by viewModel.state.collectAsState()

    LaunchedEffect(recordId) { viewModel.load(recordId) }

    LaunchedEffect(state.loaded, state.record) {
        if (state.loaded && state.record == null) onBack()
    }

    val record = state.record
    if (record != null) {
        RecordDetailScreen(
            record = record,
            actions = RecordDetailActions(
                onBack = onBack,
                onEdit = onEdit,
                onDelete = viewModel::delete
            )
        )
    }
}