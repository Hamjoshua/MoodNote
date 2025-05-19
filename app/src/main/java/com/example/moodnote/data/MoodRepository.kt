package com.example.moodnote.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val emotionDao: EmotionDao
) {
    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun getAllEmotions(): LiveData<List<Emotion>> {
        return emotionDao.getAllEmotions()
    }

    fun removeNote(note: Note) {
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
        emotionIdList: List<Int>?, event: String?
    ): LiveData<List<Note>> {
        return noteDao.getNotesByFilter(dateFrom, dateTo, emotionIdList, event)
    }
}