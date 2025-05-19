package com.example.moodnote.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface EmotionDao {
    @Query("Select * from Emotion")
    fun getAllEmotions() : List<Emotion>

    @Insert(onConflict = REPLACE)
    fun insertOrReplaceEmotion(emotion: Emotion)
}