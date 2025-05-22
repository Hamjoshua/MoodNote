package com.example.moodnote.vm

import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.moodnote.data.Emotion
import com.example.moodnote.data.MoodRepository
import com.example.moodnote.data.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {
    private val _emotions: MutableStateFlow<List<Emotion>> = MutableStateFlow(emptyList())
    private val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    private var _emotionIdList: List<Int> = emptyList()
    val emotions: StateFlow<List<Emotion>> = _emotions.asStateFlow()
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            moodRepository.getAllEmotions().collect {
                _emotions.value = it
                _emotionIdList = it.map { it.id }
            }
        }

        clearFilter()
    }

    fun clearFilter() {
        updateNotes(null, null, _emotionIdList, null)
    }

    fun updateNotes(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>, event: String?
    ) {
        viewModelScope.launch {
            moodRepository.getAllNotes()
                .map {
                    it.filter {
                        note ->
                        note.emotionId in _emotionIdList &&
                                (note.date > (dateFrom ?: 0L) && note.date < (dateTo ?: 9999L)) &&
                                note.event.contains((event ?: ""))
                    }
                }
                .collect {
                    _notes.value = it
                }
        }
    }

    fun addOrEditNewNote(note: Note) {
        moodRepository.insertOrReplaceNote(note)
    }

    fun getNote(id: Int): Note {
        return moodRepository.getNote(id)
    }

    fun removeNote(note: Note) {
        moodRepository.deleteNote(note)
    }
}