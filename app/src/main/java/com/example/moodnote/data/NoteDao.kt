package com.example.moodnote.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("Select * from Note")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("Select * from Note where id = (:id)")
    fun getNote(id: Int) : Note

    @Insert(onConflict = REPLACE)
    fun insertOrReplace(note: Note)

    @Query("Select * from Note where" +
            "(:dateFrom is null or date > :dateFrom) and" +
            "(:dateTo is null or date < :dateTo) and" +
            "(:emotionIdList is null or emotionId in (:emotionIdList)) and" +
            "(:event is null or event like :event)")
    fun getNotesByFilter(dateFrom: Long?, dateTo: Long?,
                         emotionIdList: List<Int>?,
                         event: String?) : LiveData<List<Note>>

    @Delete
    fun deleteNote(note: Note)
}