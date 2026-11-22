package com.example.medicare_assistant.profile.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.profile.view.ProfileActions
import com.example.medicare_assistant.profile.view.ProfileScreen

@Composable
fun ProfileRoute(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenAllergies: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.loaded) {
        ProfileScreen(
            state = state,
            actions = ProfileActions(

                // ===== [Back pill] ඉස්සරහ page එකට (Home) යනවා =====
                onBack = onBack,

                // ===== [Edit button] Edit profile page එක open කරනවා =====
                onEdit = onEdit,

                // ===== [Refresh location button] තාම වැඩ නෑ. Step 6 එකේදී GPS එකෙන් තැන අරන් save කරනවා =====
                onRefreshLocation = { },

                // ===== [+ Add allergy button] Allergies page එක open කරනවා (Step 7) =====
                onAddAllergy = onOpenAllergies
            )
        )
    }
}