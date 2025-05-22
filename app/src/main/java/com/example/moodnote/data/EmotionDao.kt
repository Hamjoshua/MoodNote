package com.example.moodnote.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    @Query("Select * from Emotion")
    fun getAllEmotions() : Flow<List<Emotion>>

    @Query("Select * from Emotion where id = (:id)")
    fun getEmotion(id: Int) : Emotion

    @Insert(onConflict = REPLACE)
    fun insertOrReplaceEmotion(emotion: Emotion)
}