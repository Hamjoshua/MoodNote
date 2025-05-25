package com.example.moodnote.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.moodnote.utils.AlarmHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Volatile private var INSTANCE: MoodDatabase? = null

    fun getInstance(context: Context): MoodDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: provideUserDatabase(context).also { INSTANCE = it }
        }

    @Singleton
    @Provides
    fun provideUserDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app, MoodDatabase::class.java, "MoodDatabase"
    )
        // Вставка данных после создания базы
        .addCallback(object : RoomDatabase.Callback(){
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val emotionDao : EmotionDao = getInstance(app).emotionDao()
                    Log.d("Prefill DB", emotionDao.toString())
                    defaultEmojis.forEach { emotion ->
                        Log.d("Prefill DB", emotion.toString())
                        emotionDao.insertOrReplaceEmotion(emotion)
                    }
                }
            }
        })
        //.fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideNoteDao(db: MoodDatabase) = db.noteDao()

    @Singleton
    @Provides
    fun provideEmotionDao(db: MoodDatabase) = db.emotionDao()

    @Provides
    @Singleton
    fun provideMoodRepository(noteDao: NoteDao,
                                      emotionDao: EmotionDao): MoodRepository {
        return MoodRepository(noteDao, emotionDao)
    }
}