package com.example.medicare_assistant.reminder

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.Medicine
import com.example.medicare_assistant.medicine.MedicineRepository
import com.example.medicare_assistant.settings.SettingsPrefs
import com.example.medicare_assistant.settings.withLocale
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.LuminousYellow
import com.example.medicare_assistant.ui.theme.MedicareTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AlarmActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val tag = runBlocking { SettingsPrefs(newBase).settings.first().language.tag }
        super.attachBaseContext(newBase.withLocale(tag))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOverLockScreen()
        enableEdgeToEdge()

        val medicineId = intent.getLongExtra(ReminderIntents.EXTRA_MEDICINE_ID, -1L)
        val slot = DoseSlot.entries.firstOrNull {
            it.name == intent.getStringExtra(ReminderIntents.EXTRA_SLOT)
        } ?: DoseSlot.MORNING

        val textScale = runBlocking { SettingsPrefs(this@AlarmActivity).settings.first().textSize.scale }

        setContent {
            MedicareTheme(darkTheme = false, textScale = textScale) {
                val locale = LocalConfiguration.current.locales[0]
                var loaded by remember { mutableStateOf(false) }
                var medicine by remember { mutableStateOf<Medicine?>(null) }

                LaunchedEffect(medicineId) {
                    medicine = MedicineRepository.get(applicationContext).medicines.first()
                        .firstOrNull { it.id == medicineId }
                    loaded = true
                }

                val current = medicine
                if (current != null) {
                    AlarmScreen(
                        medicine = current,
                        time = current.times[slot]?.let { formatTime(it, locale) }.orEmpty(),
                        onTaken = { act(ReminderIntents.ACTION_TAKEN, medicineId, slot) },
                        onSnooze = { act(ReminderIntents.ACTION_SNOOZE, medicineId, slot) },
                        onSkip = { act(ReminderIntents.ACTION_SKIP, medicineId, slot) }
                    )
                } else if (loaded) {
                    LaunchedEffect(Unit) { finish() }
                }
            }
        }
    }

    private fun act(action: String, medicineId: Long, slot: DoseSlot) {
        lifecycleScope.launch {
            DoseActions.handle(applicationContext, action, medicineId, slot)
            finish()
        }
    }

    private fun showOverLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
private fun AlarmScreen(
    medicine: Medicine,
    time: String,
    onTaken: () -> Unit,
    onSnooze: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LuminousYellow)
            .systemBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = time, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CapsuleIcon()
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.reminder_title),
                modifier = Modifier.semantics { heading() },
                color = Ink,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${medicine.name} ${medicine.dosage}",
                color = Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onTaken,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = LuminousYellow)
            ) {
                Text(stringResource(R.string.action_taken), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onSnooze,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Ink),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink)
            ) {
                Text(stringResource(R.string.action_snooze), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = onSkip,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_skip),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun CapsuleIcon() {
    Canvas(Modifier.size(140.dp).rotate(-40f)) {
        val w = size.width
        val h = size.height * 0.5f
        val top = (size.height - h) / 2f
        val radius = CornerRadius(h / 2f, h / 2f)
        drawRoundRect(Color.White, Offset(0f, top), Size(w, h), radius)
        val shape = Path().apply { addRoundRect(RoundRect(0f, top, w, top + h, radius)) }
        clipPath(shape) {
            drawRect(Ink, Offset(0f, top), Size(w / 2f, h))
        }
        drawRoundRect(Ink, Offset(0f, top), Size(w, h), radius, style = Stroke(6.dp.toPx()))
    }
}
