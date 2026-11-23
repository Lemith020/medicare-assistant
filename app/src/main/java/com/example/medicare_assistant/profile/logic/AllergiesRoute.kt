package com.example.medicare_assistant.profile.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.profile.view.AllergiesActions
import com.example.medicare_assistant.profile.view.AllergiesScreen

@Composable
fun AllergiesRoute(
    onBack: () -> Unit,
    viewModel: AllergiesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()


    DisposableEffect(Unit) {
        onDispose { viewModel.clearInput() }
    }

    if (state.loaded) {
        AllergiesScreen(
            state = state,
            actions = AllergiesActions(


                onBack = onBack,


                onNameChange = viewModel::onNameChange,


                onAdd = viewModel::add,


                onSuggestionClick = viewModel::onSuggestionClick,


                onRemove = viewModel::remove,


                onSaveReaction = viewModel::saveReaction
            )
        )
    }
}