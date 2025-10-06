package com.example.madexam3.data.model

import java.util.*

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val mood: Mood,
    val notes: String = "",
    val date: String, // Format: yyyy-MM-dd
    val timestamp: Long = System.currentTimeMillis()
)

enum class Mood(val displayName: String, val emoji: String, val value: Int) {
    HAPPY("Happy", "😊", 5),
    EXCITED("Excited", "🤩", 4),
    NEUTRAL("Neutral", "😐", 3),
    SAD("Sad", "😢", 2),
    ANGRY("Angry", "😠", 1)
}
