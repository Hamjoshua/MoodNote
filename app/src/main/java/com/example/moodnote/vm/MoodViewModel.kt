package com.example.moodnote.vm

import android.util.Log
import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.moodnote.data.Emotion
import com.example.moodnote.data.MoodRepository
import com.example.moodnote.data.Note
import com.example.moodnote.data.NoteWithEmotion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val _emotions: MutableStateFlow<List<Emotion>> = MutableStateFlow(emptyList())
    private val _notes: MutableSharedFlow<List<NoteWithEmotion>> = MutableSharedFlow(replay = 0)
    private var _emotionIdList: List<Int> = emptyList()
    val emotions: StateFlow<List<Emotion>> = _emotions.asStateFlow()
    val notes: SharedFlow<List<NoteWithEmotion>> = _notes

    init {
        viewModelScope.launch {
            moodRepository.getAllEmotions().collect {
                _emotions.value = it
                _emotionIdList = it.map { it.id }
            }
        }
    }

    fun clearFilter() {
        updateNotes(null, null, null, null)
    }

    fun updateNotes(
        dateFrom: Long?, dateTo: Long?,
        emotionId: Int?, event: String?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getNotesWithEmotions()
                .map {
                    Log.d("NotesVM-before", it.toString())
                    it.filter { noteEmotion ->
                        (emotionId == null || emotionId == noteEmotion.emotion.id) &&
                                (dateFrom == null || noteEmotion.note.date >= dateFrom) &&
                                (dateTo == null || noteEmotion.note.date <= dateTo) &&
                                (event == "" || noteEmotion.note.event.contains(event ?: ""))
                    }
                }
                .collect {
                    Log.d("NotesVM-after", it.toString())
                    _notes.emit(it)
                }
        }
    }

    fun addOrEditNewNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.insertOrReplaceNote(note)
        }
    }

    fun getNote(id: Long, callback: (Note) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val note = moodRepository.getNote(id)

            callback(note)

        }
    }

    fun removeNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.deleteNote(note)
        }
    }
}