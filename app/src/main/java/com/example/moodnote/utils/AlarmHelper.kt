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
import com.example.moodnote.data.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

class AlarmHelper @Inject constructor(
    @ApplicationContext private val _context: Context,
    private val appPreferences: AppPreferences) {

    companion object {
        const val ALARM_REQUEST_CODE = 100
        private const val HOURS_THRESHOLD = 21
        const val NOTIFICATION_DELAY_MS = 60_000L * 60 * HOURS_THRESHOLD
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

    fun cancelAlarm(context:Context = _context) {
        // обновляем время визита
        appPreferences.saveLastVisitTime()

        // отменяем alarm
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = createPendingIntent(context)//, PendingIntent.FLAG_NO_CREATE

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d("AlarmHelper","alarm canceled")
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
                ALARM_REQUEST_CODE,//System.currentTimeMillis().toInt() and 0xffff,
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

    fun showNotification(context: Context = _context) {
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

    fun checkAndShowNotification() {
        Log.e("AlarmHelper", " checkAndShowNotification")
        val lastVisitTime = appPreferences.getLastVisitTime()
        val currentTime = System.currentTimeMillis()
        Log.e("AlarmHelper", " diff ms ${currentTime - lastVisitTime}")
        val hoursPassed = (currentTime - lastVisitTime) / (1000 * 60 * 60)

        if (hoursPassed >= HOURS_THRESHOLD) {
            showNotification()
        }
    }

}