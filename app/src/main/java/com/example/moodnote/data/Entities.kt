package com.example.moodnote.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity
data class Emotion(
    @PrimaryKey val id: Int,
    val name: String,
    val emojiUnicode: Int,
    val emotionalWeight: Int
) {
    fun getEmojiFromUnicode(): String {
        return String(Character.toChars(emojiUnicode))
    }
}

@Entity(foreignKeys = [
    ForeignKey(
        entity = Emotion::class,
        parentColumns = ["id"],
        childColumns = ["emotionId"],
        onDelete = CASCADE
    )
])
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val emotionId: Int,
    val event: String,
    val reason: String,
    val date: Long
)