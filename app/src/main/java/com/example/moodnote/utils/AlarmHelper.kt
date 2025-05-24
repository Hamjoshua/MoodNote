package com.example.moodnote.utils
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat.canScheduleExactAlarms
import androidx.core.app.NotificationCompat
import com.example.moodnote.R
import java.util.UUID
import javax.inject.Singleton

@Singleton
class AlarmHelper(private val _context: Context) {
    // Константы
    companion object {
        const val ALARM_REQUEST_CODE = 100
        const val NOTIFICATION_DELAY_MS = 5_000L // 1 минута
    }

    fun scheduleNotification(context: Context = _context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (canScheduleExactAlarms(alarmManager)) {
            Log.e("AlarmHelper", "AlarmManager availible")
        } else {
            Log.e("AlarmHelper", "AlarmManager not availible")
            return;
        }

        val triggerTime = System.currentTimeMillis() + NOTIFICATION_DELAY_MS

        val pendingIntent = createPendingIntent(context) ?: run {
            Log.e("AlarmHelper", "Failed to create PendingIntent")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
        Log.d("AlarmHelper", "Notification scheduled")
    }

    // Отмена уведомления
    fun cancelAlarm(context:Context = _context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context)//, PendingIntent.FLAG_NO_CREATE

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    private fun createPendingIntent(context: Context): PendingIntent? {
        return try {
            // 1. Явный Intent с указанием класса Receiver
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                // Уникальное действие для каждого Intent
                action = "com.your.package.ACTION_${System.currentTimeMillis()}"

                // Добавляем данные для уникальности
                putExtra("unique_id", UUID.randomUUID().toString())

                // Для работы с неактивными приложениями
                addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            }

            // 2. Создаем PendingIntent с правильными флагами
            PendingIntent.getBroadcast(
                context,
                // Уникальный requestCode (используем хеш времени)
                System.currentTimeMillis().toInt() and 0xffff,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: Exception) {
            Log.e("AlarmHelper", "PendingIntent creation failed", e)
            null
        }
    }

    fun showNotificationNow(context: Context = _context) {//context: Context
        // 1. Создаем NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // 2. Создаем канал (обязательно для Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "instant_channel",
                "Мгновенные уведомления",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Канал для мгновенных уведомлений"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 3. Создаем само уведомление
        val notification = NotificationCompat.Builder(context, "instant_channel")
            .setContentTitle("Срочное уведомление!")
            .setContentText("Это сообщение показывается прямо сейчас")
            .setSmallIcon(R.drawable.ic_launcher_background) // Обязательно!
            .setPriority(NotificationCompat.PRIORITY_MAX) // Для Android 7.1 и ниже
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

}