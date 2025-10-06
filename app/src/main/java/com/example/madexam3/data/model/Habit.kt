package com.example.madexam3.data.model

import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val trackingType: TrackingType,
    val targetValue: Int = 1,
    val unit: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class TrackingType {
    CHECKBOX,  // Simple done/not done
    COUNTER,   // Number tracking (e.g., glasses of water)
    TIMER      // Time tracking (e.g., meditation minutes)
}

data class HabitCompletion(
    val habitId: String,
    val date: String, // Format: yyyy-MM-dd
    val value: Int,
    val timestamp: Long = System.currentTimeMillis()
)
