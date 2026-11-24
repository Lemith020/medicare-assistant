package com.example.medicare_assistant.records.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.records.view.RecordsActions
import com.example.medicare_assistant.records.view.RecordsScreen

@Composable
fun RecordsRoute(
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    viewModel: RecordsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.loaded) {
        RecordsScreen(
            state = state,
            actions = RecordsActions(
                onFilterChange = viewModel::setFilter,
                onToggleSearch = viewModel::toggleSearch,
                onQueryChange = viewModel::setQuery,
                onOpen = onOpen,
                onAdd = onAdd
            )
        )
    }
}