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
import com.example.moodnote.data.AppPreferences

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val alarmHelper = AlarmHelper(
            context.applicationContext,
            AppPreferences(
                context.applicationContext.getSharedPreferences(
                    "AppPrefs",
                    Context.MODE_PRIVATE
                )
            )
        )
        alarmHelper.checkAndShowNotification()
    }

    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 123
    }
}