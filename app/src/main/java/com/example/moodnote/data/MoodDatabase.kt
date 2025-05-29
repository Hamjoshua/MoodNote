package com.example.moodnote.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Emotion::class, Note::class], version = 2, exportSchema = false)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun noteDao() : NoteDao
    abstract fun emotionDao() : EmotionDao
}