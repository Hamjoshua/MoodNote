package com.example.moodnote.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun saveLastVisitTime() {
        sharedPreferences.edit()
            .putLong("LAST_VISIT_TIME", System.currentTimeMillis())
            .apply()
    }

    fun getLastVisitTime(): Long {
        return sharedPreferences.getLong("LAST_VISIT_TIME", 0L)
    }
}