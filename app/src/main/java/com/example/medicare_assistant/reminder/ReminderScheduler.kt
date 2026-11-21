package com.example.medicare_assistant.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.Medicine
import java.time.LocalTime
import java.time.ZonedDateTime

object ReminderScheduler {

    fun requestCode(medicineId: Long, slot: DoseSlot, snooze: Boolean = false): Int {
        val base = (medicineId % 10_000_000L).toInt() * 8 + slot.ordinal
        return if (snooze) base + 4 else base
    }

    fun scheduleAll(context: Context, medicines: List<Medicine>) {
        medicines.forEach { schedule(context, it, cancelFirst = false) }
    }

    fun schedule(context: Context, medicine: Medicine, cancelFirst: Boolean = true) {
        if (cancelFirst) cancel(context, medicine.id)
        medicine.times.keys.forEach { scheduleNext(context, medicine, it) }
    }

    fun scheduleNext(context: Context, medicine: Medicine, slot: DoseSlot) {
        val time = medicine.times[slot] ?: return
        val trigger = nextTrigger(time)
        val end = medicine.endDate
        if (end != null && trigger.toLocalDate().isAfter(end)) return
        setAlarm(context, medicine.id, slot, trigger.toInstant().toEpochMilli(), snooze = false)
    }

    fun snooze(context: Context, medicineId: Long, slot: DoseSlot, minutes: Long = 10) {
        setAlarm(context, medicineId, slot, System.currentTimeMillis() + minutes * 60_000L, snooze = true)
    }

    fun cancel(context: Context, medicineId: Long) {
        val manager = context.getSystemService(AlarmManager::class.java)
        DoseSlot.entries.forEach { slot ->
            listOf(false, true).forEach { snooze ->
                val pending = pendingIntent(context, medicineId, slot, snooze, noCreate = true)
                if (pending != null) {
                    manager.cancel(pending)
                    pending.cancel()
                }
            }
        }
    }

    private fun nextTrigger(time: LocalTime): ZonedDateTime {
        val now = ZonedDateTime.now()
        val candidate = now.toLocalDate().atTime(time).atZone(now.zone)
        return if (candidate.isAfter(now)) candidate else candidate.plusDays(1)
    }

    private fun setAlarm(
        context: Context,
        medicineId: Long,
        slot: DoseSlot,
        triggerAtMillis: Long,
        snooze: Boolean
    ) {
        val manager = context.getSystemService(AlarmManager::class.java)
        val pending = pendingIntent(context, medicineId, slot, snooze)!!
        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()
        try {
            if (canExact) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            } else {
                manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
            }
        } catch (e: SecurityException) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
        }
    }

    private fun pendingIntent(
        context: Context,
        medicineId: Long,
        slot: DoseSlot,
        snooze: Boolean,
        noCreate: Boolean = false
    ): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java)
            .putExtra(ReminderIntents.EXTRA_MEDICINE_ID, medicineId)
            .putExtra(ReminderIntents.EXTRA_SLOT, slot.name)
            .putExtra(ReminderIntents.EXTRA_SNOOZE, snooze)
        val flags = PendingIntent.FLAG_IMMUTABLE or
                if (noCreate) PendingIntent.FLAG_NO_CREATE else PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getBroadcast(context, requestCode(medicineId, slot, snooze), intent, flags)
    }
}