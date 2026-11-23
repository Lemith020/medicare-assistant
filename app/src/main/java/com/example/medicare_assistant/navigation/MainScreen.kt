package com.example.medicare_assistant.navigation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.history.HistoryScreen
import com.example.medicare_assistant.home.HomeScreen
import com.example.medicare_assistant.medicine.AddMedicineScreen
import com.example.medicare_assistant.medicine.MedicinesScreen
import com.example.medicare_assistant.profile.logic.ProfileRoute
import com.example.medicare_assistant.profile.logic.ProfileViewModel
import com.example.medicare_assistant.profile.view.components.PageTopBar
import com.example.medicare_assistant.reminder.ReminderPermissionBanner
import com.example.medicare_assistant.settings.SettingsScreen
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.profile.logic.EditProfileRoute
import com.example.medicare_assistant.profile.logic.AllergiesRoute
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
enum class MainTab(@StringRes val labelRes: Int) {
    TODAY(R.string.nav_today),
    MEDICINES(R.string.nav_medicines),
    HISTORY(R.string.nav_history),
    SETTINGS(R.string.nav_settings)
}

@Composable
fun MainScreen() {
    var tab by rememberSaveable { mutableStateOf(MainTab.TODAY) }
    var overlay by rememberSaveable { mutableStateOf(Overlay.NONE) }
    var editId by rememberSaveable { mutableStateOf<Long?>(null) }
    var formKey by rememberSaveable { mutableIntStateOf(0) }

    val profileViewModel: ProfileViewModel = viewModel()
    val profile by profileViewModel.state.collectAsState()

    fun openForm(id: Long?) {
        editId = id
        formKey += 1
        overlay = Overlay.ADD_MEDICINE
    }


    BackHandler(enabled = overlay != Overlay.NONE) {
        overlay = overlay.parent ?: Overlay.NONE
    }

    when (overlay) {

        // ===== [Add / Edit medicine page] =====
        Overlay.ADD_MEDICINE -> AddMedicineScreen(
            editId = editId,
            formKey = formKey,
            onBack = { overlay = Overlay.NONE },
            onSaved = {
                overlay = Overlay.NONE
                tab = MainTab.MEDICINES
            }
        )


        Overlay.PROFILE -> ProfileRoute(
            onBack = { overlay = Overlay.NONE },
            onEdit = {
                formKey += 1
                overlay = Overlay.EDIT_PROFILE
            },
            onOpenAllergies = { overlay = Overlay.ALLERGIES }
        )


        Overlay.EDIT_PROFILE -> EditProfileRoute(
            formKey = formKey,
            onBack = { overlay = Overlay.PROFILE },
            onSaved = { overlay = Overlay.PROFILE }
        )



        Overlay.ALLERGIES -> AllergiesRoute(
            onBack = { overlay = Overlay.PROFILE }
        )

        // ===== [Main tabs] Today / Medicines / History / Settings =====
        Overlay.NONE -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
        ) {
            ReminderPermissionBanner()
            Box(Modifier.weight(1f)) {
                when (tab) {
                    MainTab.TODAY -> HomeScreen(
                        onAddMedicine = { openForm(null) },
                        onOpenProfile = { overlay = Overlay.PROFILE },
                        avatarInitial = profile.initial,
                        avatarPhotoPath = profile.photoPath
                    )
                    MainTab.MEDICINES -> MedicinesScreen(
                        onAddClick = { openForm(null) },
                        onEditClick = { openForm(it) }
                    )
                    MainTab.HISTORY -> HistoryScreen()
                    MainTab.SETTINGS -> SettingsScreen()
                }
            }
            BottomBar(selected = tab, onSelect = { tab = it })
        }
    }
}

@Composable
private fun BottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    val density = LocalDensity.current


    CompositionLocalProvider(
        LocalDensity provides Density(density.density, minOf(density.fontScale, 1.15f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MainTab.entries.forEach { tab ->
                val isSelected = tab == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) LuminousYellow else Color.Transparent)
                        .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(tab) }),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(tab.labelRes),
                        color = if (isSelected) Ink else MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ComingSoonPage(@StringRes title: Int, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(20.dp)
    ) {
        PageTopBar(onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(title),
            modifier = Modifier.semantics { heading() },
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.coming_soon),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp
        )
    }
}