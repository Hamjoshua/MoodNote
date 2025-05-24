package com.example.moodnote.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val emotionDao: EmotionDao
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
        noteDao.insertOrReplace(note)
    }

    fun getNote(id: Int): Note {
        return noteDao.getNote(id)
    }

    fun getNotesByFilter(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>, event: String?
    ): Flow<List<Note>> {
        return noteDao.getNotesByFilter(dateFrom, dateTo, emotionIdList, event)
    }

    fun getNotesWithEmotions() : Flow<List<NoteWithEmotion>>{
        return noteDao.getNotesWithEmotions()
    }
}