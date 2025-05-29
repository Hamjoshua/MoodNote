package com.example.moodnote.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodnote.data.EmojiCountResult
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExtendedMoodViewModel @Inject constructor(
    moodRepository: MoodRepository
) : MoodViewModel(moodRepository) {

    fun getLastNotes(lastCount: Long, callback: (List<Note>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val listNote = moodRepository.getLastNotes(lastCount)
            callback(listNote)
        }
    }

    fun getAllEmotions(
         callback: (List<Note>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getAllNotes().collect {
                it-> callback(it)
            }
        }
    }

    fun getDistinctEmotionIdsByFilter(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>?, event: String?, callback: (List<Int>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getDistinctEmotionIdsByFilter(dateFrom, dateTo, emotionIdList, event).collect { emotionIds ->
                callback(emotionIds)
            }
        }
    }

    fun getDistinctEmotionEmojiCodeIdsByFilter(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>?, event: String?, callback: (List<Int>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getDistinctEmotionEmojiCodeIdsByFilter(dateFrom, dateTo, emotionIdList, event).collect { emotionIds ->
                callback(emotionIds)
            }
        }
    }

    fun getEmotionEmojiCodeCountIdsByFilter(
        dateFrom: Long?, dateTo: Long?,
        emotionIdList: List<Int>?, event: String?, callback: (List<EmojiCountResult>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moodRepository.getEmotionEmojiCodeCountIdsByFilter(dateFrom, dateTo, emotionIdList, event).collect { emotionIds ->
                callback(emotionIds)
            }
        }
    }

}