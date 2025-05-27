package com.example.moodnote.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class MoodRepository @Inject constructor(
    protected val noteDao: NoteDao,
    protected val emotionDao: EmotionDao
) {
    fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun getAllEmotions(): Flow<List<Emotion>> {
        return emotionDao.getAllEmotions()
    }

    fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun insertOrReplaceNote(note: Note) {
        Calendar.getInstance().timeInMillis
        noteDao.insertOrReplace(note)
    }

    fun getNote(id: Long): Note {
        return noteDao.getNote(id)
    }

    fun getNotesByFilter(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>, event: String?
    ): Flow<List<Note>> {

        return noteDao.getNotesByFilter(dateFrom, dateTo, emotionIdList, event)
    }

    fun getDistinctEmotionIdsByFilter(
        dateFrom: Long?,
        dateTo: Long?,
        emotionIdList: List<Int>?,
        event: String?
    ): Flow<List<Int>> {
        val list = emotionIdList ?: emptyList()
        return noteDao.getDistinctEmotionIdsByFilter(dateFrom, dateTo, list, list.size, event)
    }

    fun getLastNotes(numberNotes:Long): List<Note> {
        return noteDao.getLastNotes(numberNotes)
    }

    fun getNotesWithEmotions() : Flow<List<NoteWithEmotion>>{
        return noteDao.getNotesWithEmotions()
    }
}