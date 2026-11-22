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

    // ===== [Page එකෙන් පිටවීම] ටයිප් කරලා තිබ්බ නම ඊළඟ වතාවට පෙනෙන්නේ නැති වෙන්න clear කරනවා =====
    DisposableEffect(Unit) {
        onDispose { viewModel.clearInput() }
    }

    if (state.loaded) {
        AllergiesScreen(
            state = state,
            actions = AllergiesActions(

                // ===== [Back pill] Profile page එකට යනවා =====
                onBack = onBack,

                // ===== [Medicine name field] =====
                onNameChange = viewModel::onNameChange,

                // ===== [Add button / keyboard Done] ලැයිස්තුවට එකතු කරනවා =====
                onAdd = viewModel::add,

                // ===== [Suggestion chips] ඔබපු නම field එකට පුරවනවා =====
                onSuggestionClick = viewModel::onSuggestionClick,

                // ===== [Remove button] තහවුරු කළාම ලැයිස්තුවෙන් ඉවත් කරනවා =====
                onRemove = viewModel::remove,

                // ===== [Allergy card tap → Reaction dialog Save] ප්‍රතික්‍රියාව save කරනවා =====
                onSaveReaction = viewModel::saveReaction
            )
        )
    }
}