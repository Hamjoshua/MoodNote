package com.example.moodnote.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moodnote.R
import com.example.moodnote.data.Note
import com.example.moodnote.databinding.RItemBinding

class NoteViewHolder(binding: RItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val binding = binding
}

class NoteAdapter : ListAdapter<Note, NoteViewHolder>(NoteDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = RItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = currentList[position]
        holder.binding.emotionShowerText = note.emotionId
        holder.binding.eventShowerText.text = note.event
        holder.binding.dateShowerText.text = note.date.toString()
    }
}

interface OnNoteElementClick {
    fun onClick(note: Note)
}

class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.event == newItem.event;
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem;
    }
}

