package com.example.medicare_assistant.reminder

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.example.medicare_assistant.dose.DoseLogRepository
import com.example.medicare_assistant.dose.DoseStatus
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.MedicineRepository
import com.example.medicare_assistant.settings.SettingsPrefs
import com.example.medicare_assistant.settings.withLocale
import kotlinx.coroutines.flow.first
import java.time.LocalDate

object DoseActions {

    suspend fun handle(context: Context, action: String?, medicineId: Long, slot: DoseSlot) {
        val app = context.applicationContext
        when (action) {
            ReminderIntents.ACTION_TAKEN -> mark(app, medicineId, slot, DoseStatus.TAKEN)
            ReminderIntents.ACTION_SKIP -> mark(app, medicineId, slot, DoseStatus.SKIPPED)
            ReminderIntents.ACTION_SNOOZE -> {
                ReminderScheduler.snooze(app, medicineId, slot)
                NotificationManagerCompat.from(app).cancel(ReminderScheduler.requestCode(medicineId, slot))
            }
        }
    }

    suspend fun mark(context: Context, medicineId: Long, slot: DoseSlot, status: DoseStatus) {
        val app = context.applicationContext
        val log = DoseLogRepository.get(app)
        val key = DoseLogRepository.key(LocalDate.now(), medicineId, slot)
        val previous = log.statusOf(key)

        log.record(key, status)
        NotificationManagerCompat.from(app).cancel(ReminderScheduler.requestCode(medicineId, slot))

        if (status == DoseStatus.TAKEN && previous != DoseStatus.TAKEN) {
            val medicine = MedicineRepository.get(app).decrementStock(medicineId)
            if (medicine != null && (medicine.stock == medicine.lowStockThreshold || medicine.stock == 0)) {
                val tag = SettingsPrefs(app).settings.first().language.tag
                NotificationHelper.showLowStock(app.withLocale(tag), medicine)
            }
        }
    }
}