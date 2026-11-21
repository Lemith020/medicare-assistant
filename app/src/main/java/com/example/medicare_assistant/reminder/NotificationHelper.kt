package com.example.medicare_assistant.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.medicare_assistant.MainActivity
import com.example.medicare_assistant.R
import com.example.medicare_assistant.core.formatTime
import com.example.medicare_assistant.medicine.DoseSlot
import com.example.medicare_assistant.medicine.Medicine

object NotificationHelper {

    private const val CHANNEL_REMINDERS = "medicine_reminders"
    private const val CHANNEL_STOCK = "low_stock"

    private fun canNotify(context: Context): Boolean {
        val permitted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        return permitted && NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        if (manager.getNotificationChannel(CHANNEL_REMINDERS) == null) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val channel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.channel_reminders),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_reminders_desc)
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), attributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 600, 300, 600, 300, 600, 300, 600)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }

        if (manager.getNotificationChannel(CHANNEL_STOCK) == null) {
            val channel = NotificationChannel(
                CHANNEL_STOCK,
                context.getString(R.string.channel_stock),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_stock_desc)
            }
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun showReminder(context: Context, medicine: Medicine, slot: DoseSlot) {
        if (!canNotify(context)) return
        ensureChannels(context)

        val time = medicine.times[slot] ?: return
        val locale = context.resources.configuration.locales[0]
        val id = ReminderScheduler.requestCode(medicine.id, slot)

        val alarmIntent = Intent(context, AlarmActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(ReminderIntents.EXTRA_MEDICINE_ID, medicine.id)
            .putExtra(ReminderIntents.EXTRA_SLOT, slot.name)
        val screen = PendingIntent.getActivity(
            context,
            id,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.reminder_title))
            .setContentText("${medicine.name} ${medicine.dosage} · ${formatTime(time, locale)}")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setTimeoutAfter(10 * 60 * 1000L)
            .setContentIntent(screen)
            .setFullScreenIntent(screen, true)
            .addAction(0, context.getString(R.string.action_taken), action(context, ReminderIntents.ACTION_TAKEN, medicine.id, slot, id))
            .addAction(0, context.getString(R.string.action_snooze), action(context, ReminderIntents.ACTION_SNOOZE, medicine.id, slot, id))
            .addAction(0, context.getString(R.string.action_skip), action(context, ReminderIntents.ACTION_SKIP, medicine.id, slot, id))
            .build()

        notification.flags = notification.flags or Notification.FLAG_INSISTENT
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    @SuppressLint("MissingPermission")
    fun showLowStock(context: Context, medicine: Medicine) {
        if (!canNotify(context)) return
        ensureChannels(context)

        val stockText = context.resources.getQuantityString(R.plurals.tablets_left, medicine.stock, medicine.stock)
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_STOCK)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notif_low_stock_title))
            .setContentText("${medicine.name} · $stockText")
            .setAutoCancel(true)
            .setContentIntent(open)
            .build()

        NotificationManagerCompat.from(context).notify("low_${medicine.id}".hashCode(), notification)
    }

    private fun action(
        context: Context,
        action: String,
        medicineId: Long,
        slot: DoseSlot,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, DoseActionReceiver::class.java)
            .setAction(action)
            .putExtra(ReminderIntents.EXTRA_MEDICINE_ID, medicineId)
            .putExtra(ReminderIntents.EXTRA_SLOT, slot.name)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}