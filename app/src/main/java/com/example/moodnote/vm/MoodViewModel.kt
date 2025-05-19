package com.example.moodnote.vm

import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.moodnote.data.Emotion
import com.example.moodnote.data.MoodRepository
import com.example.moodnote.data.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val moodRepository: MoodRepository
) : ViewModel() {
    private val _emotions: LiveData<List<Emotion>> = moodRepository.getAllEmotions()
    private val _notes: LiveData<List<Note>> = moodRepository.getAllNotes()
    val filteredNotes: LiveData<List<Note>> = _notes.map{
        it.filter {
            it.date > (filterDateFrom.value ?: 0) && it.date < (filterDateTo.value ?: 9999L)
            it.event.contains(filterEvent.value ?: "")
            it.emotionId in (filterEmotionIds.value ?: _emotions.value!!.map { it.id })
        }
    }

    val filterEmotionIds : MutableLiveData<List<Int>> = MutableLiveData()
    val filterDateFrom : MutableLiveData<Long> = MutableLiveData()
    val filterDateTo : MutableLiveData<Long> = MutableLiveData()
    val filterEvent : MutableLiveData<String> = MutableLiveData()

    fun addNewNote(note: Note) {
        moodRepository.insertOrReplaceNote(note)
    }

    fun editNode() {

    }
}