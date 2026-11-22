package com.example.medicare_assistant.profile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medicare_assistant.R
import com.example.medicare_assistant.profile.view.components.AllergyCard
import com.example.medicare_assistant.profile.view.components.InfoCard
import com.example.medicare_assistant.profile.view.components.LocationCard
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.ui.components.ProfileAvatar
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow

data class ProfileUiState(
    val loaded: Boolean = false,
    val isEmpty: Boolean = true,
    val name: String = "",
    val initial: String? = null,
    val age: Int? = null,
    val phone: String = "",
    val address: String = "",
    val locationText: String = "",
    val locationUpdatedAt: Long? = null,
    val allergies: List<String> = emptyList()
)

data class ProfileActions(
    val onBack: () -> Unit,
    val onEdit: () -> Unit,
    val onRefreshLocation: () -> Unit,
    val onAddAllergy: () -> Unit
)

@Composable
fun ProfileScreen(state: ProfileUiState, actions: ProfileActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PageTopBar(
                onBack = actions.onBack,
                trailing = { EditButton(onClick = actions.onEdit) }
            )

            ProfileHeader(state)

            if (state.isEmpty) EmptyHint()

            InfoCard(label = stringResource(R.string.profile_phone), value = state.phone)
            InfoCard(label = stringResource(R.string.profile_address), value = state.address)
            LocationCard(
                locationText = state.locationText,
                updatedAt = state.locationUpdatedAt,
                onRefresh = actions.onRefreshLocation
            )
            AllergyCard(allergies = state.allergies, onAdd = actions.onAddAllergy)
        }
    }
}

@Composable
private fun EditButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LuminousYellow, contentColor = Ink)
    ) {
        Text(
            text = stringResource(R.string.action_edit),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    val displayName = state.name.ifBlank { stringResource(R.string.profile_name_placeholder) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAvatar(
            initial = state.initial,
            contentDescription = displayName,
            diameter = 104.dp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = displayName,
            modifier = Modifier.semantics { heading() },
            color = if (state.name.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        state.age?.let {
            Text(
                text = stringResource(R.string.profile_age, it),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun EmptyHint() {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_empty_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.profile_empty_body),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp
        )
    }
}