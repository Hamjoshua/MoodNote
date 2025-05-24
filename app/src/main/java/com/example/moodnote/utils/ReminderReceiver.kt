package com.example.moodnote.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moodnote.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ReminderReceiver", "Notification creating")
        showNotificationNow(context)
    }

    fun showNotificationNow(context: Context) {//context: Context
        // 1. Создаем NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // 2. Создаем канал (обязательно для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "instant_channel",
                "Уведомления с задержкой",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для уведомлений с задержкой"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 3. Создаем само уведомление
        val notification = NotificationCompat.Builder(context, "instant_channel")
            .setContentTitle("Напоминание")
            .setContentText("Вы давно не оставляли эмоцию")
            .setSmallIcon(R.drawable.ic_launcher_background) // Обязательно!
            .setPriority(NotificationCompat.PRIORITY_MAX) // Для Android 7.1 и ниже
            .setAutoCancel(true)
            .build()

        // 4. Показываем уведомление
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }


    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 123
    }
}