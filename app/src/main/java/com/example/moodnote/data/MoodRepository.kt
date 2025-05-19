package com.example.moodnote.data

import javax.inject.Inject

class MoodRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val emotionDao: EmotionDao
) {
    fun getAllNotes(): List<Note> {
        return noteDao.getAllNotes()
    }

    fun getAllEmotions(): List<Emotion> {
        return emotionDao.getAllEmotions()
    }

    fun removeNote(note: Note){
        noteDao.deleteNote(note)
    }

    fun insertOrReplaceNote(note: Note){
        noteDao.insertOrReplace(note)
    }

    fun getNotesByFilter(dateFrom: Long?, dateTo: Long?,
                         emotionIdList: List<Int>?, event: String?) : List<Note>{
        return noteDao.getNotesByFilter(dateFrom, dateTo, emotionIdList, event)
    }
}