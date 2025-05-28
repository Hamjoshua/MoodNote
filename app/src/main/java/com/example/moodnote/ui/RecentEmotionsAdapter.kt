package com.example.moodnote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.moodnote.R
import com.example.moodnote.data.Emotion
import com.example.moodnote.data.Note
import com.example.moodnote.vm.ExtendedMoodViewModel
import com.example.moodnote.vm.MoodViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EmotionData (
    val emotionType: String,
    var date: String,
    val note: String? = null
)

class RecentEmotionsAdapter(
    private val viewModel: ExtendedMoodViewModel
) :
    RecyclerView.Adapter<RecentEmotionsAdapter.ViewHolder>() {

    var _listNotes: List<Note> = emptyList()

    init {
        viewModel.getLastNotes(10) { notes ->
            _listNotes = notes
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(emotion: EmotionData) {
            itemView.findViewById<TextView>(R.id.emotionType).text = emotion.emotionType
            itemView.findViewById<TextView>(R.id.emotionDate).text = emotion.date
            itemView.findViewById<TextView>(R.id.emotionNote).text = emotion.note
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emotion, parent, false)
        return ViewHolder(view)
    }

    fun getEmojiFromUnicode(emotion: Emotion): String {
        return String(Character.toChars(emotion.emojiUnicode))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = _listNotes[position]
        val listEmotions = viewModel.emotions.value;
        val emotion = listEmotions.find { it.id == note.emotionId } ?: Emotion.DEFAULT

        val date = Date(note.date)
        val format = SimpleDateFormat("mm-dd", Locale.getDefault())
        val dateString = format.format(date)

        var emotionData = EmotionData(getEmojiFromUnicode(emotion),dateString,note.reason)
        holder.bind(emotionData)
    }

    override fun getItemCount() = _listNotes.size
}