package com.example.medicare_assistant.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val doses by viewModel.doses.collectAsState()
    val locale = LocalConfiguration.current.locales[0]

    val sorted = remember(doses) { doses.sortedBy { it.time } }
    val next = sorted.firstOrNull { it.status == DoseStatus.UPCOMING }
    val others = sorted.filter { it != next }
    val takenCount = doses.count { it.status == DoseStatus.TAKEN }

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> R.string.greeting_morning
        in 12..16 -> R.string.greeting_afternoon
        else -> R.string.greeting_evening
    }
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", locale))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
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
            Text(
                text = stringResource(R.string.home_subtitle, takenCount, doses.size),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }

        if (next != null) {
            NextDoseCard(
                dose = next,
                locale = locale,
                onTaken = { viewModel.mark(next.id, DoseStatus.TAKEN) },
                onSkip = { viewModel.mark(next.id, DoseStatus.SKIPPED) }
            )
        } else {
            AllDoneCard()
        }

        LowStockBanner(name = viewModel.lowStockName, count = viewModel.lowStockCount)

        others.forEach { DoseRow(dose = it, locale = locale) }
    }
}