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
import com.example.medicare_assistant.R
import com.example.medicare_assistant.home.HomeScreen
import com.example.medicare_assistant.medicine.AddMedicineScreen
import com.example.medicare_assistant.medicine.MedicinesScreen
import com.example.medicare_assistant.settings.SettingsScreen
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.reminder.ReminderPermissionBanner
import com.example.medicare_assistant.history.HistoryScreen

enum class MainTab(@StringRes val labelRes: Int) {
    TODAY(R.string.nav_today),
    MEDICINES(R.string.nav_medicines),
    HISTORY(R.string.nav_history),
    SETTINGS(R.string.nav_settings)
}

@Composable
fun MainScreen() {
    var tab by rememberSaveable { mutableStateOf(MainTab.TODAY) }
    var adding by rememberSaveable { mutableStateOf(false) }
    var editId by rememberSaveable { mutableStateOf<Long?>(null) }
    var formKey by rememberSaveable { mutableIntStateOf(0) }

    fun openForm(id: Long?) {
        editId = id
        formKey += 1
        adding = true
    }

    BackHandler(enabled = adding) { adding = false }

    if (adding) {
        AddMedicineScreen(
            editId = editId,
            formKey = formKey,
            onBack = { adding = false },
            onSaved = {
                adding = false
                tab = MainTab.MEDICINES
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
        ) {
            ReminderPermissionBanner()
            Box(Modifier.weight(1f)) {
                when (tab) {
                    MainTab.TODAY -> HomeScreen(onAddMedicine = { openForm(null) })
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
