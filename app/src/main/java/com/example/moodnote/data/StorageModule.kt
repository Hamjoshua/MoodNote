package com.example.moodnote.data

import android.content.Context
import android.content.SharedPreferences
import com.example.moodnote.utils.AlarmHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "AppPrefs",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferences(
        sharedPreferences: SharedPreferences
    ): AppPreferences {
        return AppPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAlarmHelper(
        @ApplicationContext context: Context,
        appPreferences: AppPreferences
    ): AlarmHelper {
        return AlarmHelper(context, appPreferences)
    }
}