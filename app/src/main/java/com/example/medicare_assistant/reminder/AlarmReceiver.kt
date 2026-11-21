package com.example.medicare_assistant.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medicare_assistant.dose.DoseLogRepository
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.MedicineRepository
import com.example.medicare_assistant.settings.SettingsPrefs
import com.example.medicare_assistant.settings.withLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra(ReminderIntents.EXTRA_MEDICINE_ID, -1L)
        val slot = DoseSlot.entries.firstOrNull {
            it.name == intent.getStringExtra(ReminderIntents.EXTRA_SLOT)
        } ?: return
        val snooze = intent.getBooleanExtra(ReminderIntents.EXTRA_SNOOZE, false)
        val app = context.applicationContext
        val pending = goAsync()

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val medicine = MedicineRepository.get(app).medicines.first().firstOrNull { it.id == medicineId }
                if (medicine != null && medicine.times.containsKey(slot)) {
                    if (!snooze) ReminderScheduler.scheduleNext(app, medicine, slot)

                    val today = LocalDate.now()
                    val status = DoseLogRepository.get(app)
                        .statusOf(DoseLogRepository.key(today, medicine.id, slot))
                    val finished = status == DoseStatus.TAKEN || status == DoseStatus.SKIPPED

                    if (!medicine.isCompleted(today) && !finished) {
                        val tag = SettingsPrefs(app).settings.first().language.tag
                        NotificationHelper.showReminder(app.withLocale(tag), medicine, slot)
                    }
                }
            } finally {
                pending.finish()
            }
        }
    }
}