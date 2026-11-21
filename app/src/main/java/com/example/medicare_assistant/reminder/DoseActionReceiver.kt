package com.example.medicare_assistant.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medicare_assistant.medicine.DoseSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DoseActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicineId = intent.getLongExtra(ReminderIntents.EXTRA_MEDICINE_ID, -1L)
        val slot = DoseSlot.entries.firstOrNull {
            it.name == intent.getStringExtra(ReminderIntents.EXTRA_SLOT)
        } ?: return
        val app = context.applicationContext
        val pending = goAsync()

        CoroutineScope(Dispatchers.Default).launch {
            try {
                DoseActions.handle(app, intent.action, medicineId, slot)
            } finally {
                pending.finish()
            }
        }
    }
}