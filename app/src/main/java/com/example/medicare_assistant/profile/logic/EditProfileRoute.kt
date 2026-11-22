package com.example.medicare_assistant.profile.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.profile.view.EditProfileActions
import com.example.medicare_assistant.profile.view.EditProfileScreen

@Composable
fun EditProfileRoute(
    formKey: Int,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {

    val viewModel: EditProfileViewModel = viewModel(key = "edit_profile_$formKey")
    val state by viewModel.state.collectAsState()

    LaunchedEffect(formKey) { viewModel.start() }

    if (state.loaded) {
        EditProfileScreen(
            state = state,
            actions = EditProfileActions(


                onBack = onBack,


                onTakePhoto = { },


                onChoosePhoto = { },

                // ===== [Full name field] =====
                onNameChange = viewModel::onNameChange,

                // ===== [Age field] =====
                onAgeChange = viewModel::onAgeChange,

                // ===== [Phone number field] =====
                onPhoneChange = viewModel::onPhoneChange,

                // ===== [Address field] =====
                onAddressChange = viewModel::onAddressChange,

                // ===== [Use my current location button]  =====
                onUseCurrentLocation = { },


                onSave = { viewModel.save(onSaved) }
            )
        )
    }
}