package com.example.medicare_assistant.profile.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.view.components.LocationSetCard
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.profile.view.components.PhotoRow
import com.example.medicare_assistant.ui.components.AppTextField
import com.example.medicare_assistant.ui.components.PrimaryButton

data class EditProfileUiState(
    val loaded: Boolean = false,
    val name: String = "",
    val age: String = "",
    val phone: String = "",
    val address: String = "",
    val photoPath: String? = null,
    val locationText: String = "",
    val locationUpdatedAt: Long? = null,
    val locating: Boolean = false,
    @StringRes val locationMessage: Int? = null,
    @StringRes val nameError: Int? = null,
    @StringRes val ageError: Int? = null,
    @StringRes val phoneError: Int? = null
) {
    val initial: String?
        get() = name.trim().firstOrNull()?.uppercaseChar()?.toString()
}

data class EditProfileActions(
    val onBack: () -> Unit,
    val onTakePhoto: () -> Unit,
    val onChoosePhoto: () -> Unit,
    val onNameChange: (String) -> Unit,
    val onAgeChange: (String) -> Unit,
    val onPhoneChange: (String) -> Unit,
    val onAddressChange: (String) -> Unit,
    val onUseCurrentLocation: () -> Unit,
    val onSave: () -> Unit
)

@Composable
fun EditProfileScreen(state: EditProfileUiState, actions: EditProfileActions) {
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
            PageTopBar(onBack = actions.onBack)

            Text(
                text = stringResource(R.string.profile_edit_title),
                modifier = Modifier.semantics { heading() },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            PhotoRow(
                initial = state.initial,
                photoPath = state.photoPath,
                onTakePhoto = actions.onTakePhoto,
                onChoosePhoto = actions.onChoosePhoto
            )

            AppTextField(
                label = stringResource(R.string.edit_full_name),
                value = state.name,
                onValueChange = actions.onNameChange,
                placeholder = stringResource(R.string.profile_name_placeholder),
                error = state.nameError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                label = stringResource(R.string.edit_age),
                value = state.age,
                onValueChange = actions.onAgeChange,
                error = state.ageError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                label = stringResource(R.string.edit_phone),
                value = state.phone,
                onValueChange = actions.onPhoneChange,
                error = state.phoneError?.let { stringResource(it) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                label = stringResource(R.string.edit_address),
                value = state.address,
                onValueChange = actions.onAddressChange,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                )
            )

            LocationSetCard(
                locationText = state.locationText,
                updatedAt = state.locationUpdatedAt,
                locating = state.locating,
                message = state.locationMessage,
                onUseCurrentLocation = actions.onUseCurrentLocation
            )
        }

        PrimaryButton(
            text = stringResource(R.string.edit_save),
            onClick = actions.onSave,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}
