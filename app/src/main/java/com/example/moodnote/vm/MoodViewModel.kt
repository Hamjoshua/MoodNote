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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.security.auth.callback.Callback

@HiltViewModel
open class MoodViewModel @Inject constructor(
    protected val moodRepository: MoodRepository
) : ViewModel() {
    protected val _emotions: MutableStateFlow<List<Emotion>> = MutableStateFlow(emptyList())
    protected val _notes: MutableStateFlow<List<Note>> = MutableStateFlow(emptyList())
    protected var _emotionIdList: List<Int> = emptyList()
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
        updateNotes(null, null, null, null)
    }

    fun updateNotes(
        dateFrom: Long?, dateTo: Long?,
        emotionId: Int?, event: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getAllNotes()
                .map {
                    it.filter { note ->
                        ((emotionId == null && note.emotionId in _emotionIdList)
                                || (emotionId != null && note.emotionId == emotionId)) &&
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
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.insertOrReplaceNote(note)
        }
    }

    fun getNote(id: Int, callback: (Note) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = moodRepository.getNote(id)
            withContext(Dispatchers.Main){
                callback(note)
            }
        }
    }

    fun removeNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.deleteNote(note)
        }
    }
}