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
    companion object {
        // Эмоция по умолчанию (пустота/нейтраль)
        val DEFAULT = Emotion(
            id = -1,
            name = "Empty",
            emojiUnicode = 0x1F610, // 😐 (нейтральное лицо)
            emotionalWeight = 0
        )
    }

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

val defaultEmojis = listOf(
    Emotion(1, "Воодушевление", 0x1F929, 3),
    Emotion(2, "Радость", 0x1F600, 2),
    Emotion(3, "Спокойствие", 0x1F60C, 1),
    Emotion(4, "Нейтральность", 0x1F610, 0),
    Emotion(5, "Усталость", 0x1F62B, -1),
    Emotion(6, "Грусть", 0x1F61E, -2),
    Emotion(7, "Тревога", 0x1F630, -3),
    Emotion(8, "Злость", 0x1F620, -2),
    Emotion(9, "Удивление", 0x1F62E, 0),
    Emotion(10, "Благодарность", 0x1F64F, 2),
    Emotion(11, "Любовь", 0x1F60D, 3),
    Emotion(12, "Смущение", 0x1F605, -1)
)