package com.example.moodnote.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moodnote.data.Note
import com.example.moodnote.data.NoteWithEmotion
import com.example.moodnote.databinding.RItemBinding

class NoteViewHolder(binding: RItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val binding = binding
}

class NoteAdapter(val noteClick: OnNoteElementClick) :
    ListAdapter<NoteWithEmotion, NoteViewHolder>(NoteDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = RItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = currentList[position]
        holder.binding.emotionShowerText.text = note.emotion.getEmojiFromUnicode()
        holder.binding.eventShowerText.text = note.note.event
        holder.binding.dateShowerText.text = note.note.date.toDateString()
        holder.binding.root.setOnClickListener {
            noteClick.onClick(note.note)
        }
        holder.binding.removeNoteElementButton.setOnClickListener{
            noteClick.onRemoveClick(note.note)
        }
    }
}

interface OnNoteElementClick {
    fun onClick(note: Note)
    fun onRemoveClick(note: Note)
}

class NoteDiffCallback : DiffUtil.ItemCallback<NoteWithEmotion>() {
    override fun areItemsTheSame(oldItem: NoteWithEmotion, newItem: NoteWithEmotion): Boolean {
        return oldItem.note.event == newItem.note.event;
    }

    override fun areContentsTheSame(oldItem: NoteWithEmotion, newItem: NoteWithEmotion): Boolean {
        return oldItem == newItem;
    }
}

