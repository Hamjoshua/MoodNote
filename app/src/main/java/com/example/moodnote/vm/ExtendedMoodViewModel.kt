package com.example.moodnote.vm

import com.example.moodnote.data.Emotion
import com.example.moodnote.data.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExtendedMoodViewModel @Inject constructor(
    moodRepository: MoodRepository
) : MoodViewModel(moodRepository) {
    fun getEmotion(id:Int) : Emotion? {
        val emotions = _emotions.value;
        return emotions.find { it.id == id  }
    }

}