package com.example.medicare_assistant.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.ui.components.ProfileAvatar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medicare_assistant.emergency.EmergencyDialog
import com.example.medicare_assistant.emergency.EmergencyFab

@Composable
fun HomeScreen(
    onAddMedicine: () -> Unit,
    onOpenProfile: () -> Unit,
    avatarInitial: String?,
    avatarPhotoPath: String? = null,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }

    if (state.loaded) {
        HomeContent(
            state = state,
            onAddMedicine = onAddMedicine,
            onOpenProfile = onOpenProfile,
            avatarInitial = avatarInitial,
            avatarPhotoPath = avatarPhotoPath,
            viewModel = viewModel
        )
    }
}

@Composable


private fun HomeContent(
    state: HomeState,
    onAddMedicine: () -> Unit,
    onOpenProfile: () -> Unit,
    avatarInitial: String?,
    avatarPhotoPath: String?,
    viewModel: HomeViewModel
) {
    val locale = LocalConfiguration.current.locales[0]
    val doses = state.doses
    val next = doses.firstOrNull { it.status == DoseStatus.UPCOMING }
    val others = doses.filter { it != next }
    val takenCount = doses.count { it.status == DoseStatus.TAKEN }

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> R.string.greeting_morning
        in 12..16 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))
    var showEmergencyDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(greeting),
                    modifier = Modifier.semantics { heading() },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                if (doses.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.home_subtitle, takenCount, doses.size),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            }
            ProfileAvatar(
                initial = avatarInitial,
                photoPath = avatarPhotoPath,
                contentDescription = stringResource(R.string.profile_open),
                onClick = onOpenProfile
            )
        }

        state.completed.forEach { medicine ->
            CourseCompleteCard(
                medicineName = medicine.name,
                onDismiss = { viewModel.dismissCompletion(medicine.id) }
            )
        }

        when {
            doses.isEmpty() -> EmptyHomeCard(onAddMedicine = onAddMedicine)
            next != null -> NextDoseCard(
                dose = next,
                locale = locale,
                onTaken = { viewModel.mark(next, DoseStatus.TAKEN) },
                onSkip = { viewModel.mark(next, DoseStatus.SKIPPED) }
            )
            else -> AllDoneCard()
        }

        state.lowStock?.let { LowStockBanner(name = it.name, count = it.stock) }

        others.forEach { DoseRow(dose = it, locale = locale) }
    }
        EmergencyFab(
            onClick = { showEmergencyDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 16.dp)
        )

        if (showEmergencyDialog) {
            EmergencyDialog(
                onDismiss = { showEmergencyDialog = false }
            )
        }
    }
}
