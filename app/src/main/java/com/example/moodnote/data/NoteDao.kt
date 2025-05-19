package com.example.moodnote.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("Select * from Note")
    fun getAllNotes() : List<Note>

    @Insert(onConflict = REPLACE)
    fun insertOrReplace(note: Note)

    @Query("Select * from Note where" +
            "(:dateFrom is null or date > :dateFrom) and" +
            "(:dateTo is null or date < :dateTo) and" +
            "(:emotionIdList is null or emotionId in (:emotionIdList))")
    fun getNotesByFilter(dateFrom: Long?, dateTo: Long?, emotionIdList: List<Int>) : List<Note>

    @Delete
    fun deleteNote(note: Note)
}