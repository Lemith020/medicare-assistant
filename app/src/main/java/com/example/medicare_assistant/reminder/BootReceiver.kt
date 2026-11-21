package com.example.medicare_assistant.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medicare_assistant.medicine.MedicineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext
        val pending = goAsync()

        CoroutineScope(Dispatchers.Default).launch {
            try {
                ReminderScheduler.scheduleAll(app, MedicineRepository.get(app).medicines.first())
            } finally {
                pending.finish()
            }
        }
    }
}