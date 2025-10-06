package com.example.madexam3.data

import android.content.Context
import android.content.SharedPreferences
import com.example.madexam3.data.model.*
import java.text.SimpleDateFormat
import java.util.*

object DataManager {
    private const val HABITS_PREFS = "habits_prefs"
    private const val COMPLETIONS_PREFS = "completions_prefs"
    private const val MOOD_PREFS = "mood_prefs"
    private const val SETTINGS_PREFS = "settings_prefs"

    private lateinit var context: Context
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun init(context: Context) {
        this.context = context
    }

    // Habit Management using XML SharedPreferences
    fun saveHabits(habits: List<Habit>) {
        val prefs = context.getSharedPreferences(HABITS_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Clear existing habits
        editor.clear()

        // Save each habit as separate key-value pairs
        editor.putInt("habits_count", habits.size)
        habits.forEachIndexed { index, habit ->
            val prefix = "habit_$index"
            editor.putString("${prefix}_id", habit.id)
            editor.putString("${prefix}_name", habit.name)
            editor.putString("${prefix}_description", habit.description)
            editor.putString("${prefix}_trackingType", habit.trackingType.name)
            editor.putInt("${prefix}_targetValue", habit.targetValue)
            editor.putString("${prefix}_unit", habit.unit)
            editor.putLong("${prefix}_createdAt", habit.createdAt)
            editor.putBoolean("${prefix}_isActive", habit.isActive)
        }
        editor.apply()
    }

    fun getHabits(): List<Habit> {
        val prefs = context.getSharedPreferences(HABITS_PREFS, Context.MODE_PRIVATE)
        val count = prefs.getInt("habits_count", 0)
        val habits = mutableListOf<Habit>()

        for (i in 0 until count) {
            val prefix = "habit_$i"
            val id = prefs.getString("${prefix}_id", "") ?: ""
            val name = prefs.getString("${prefix}_name", "") ?: ""
            val description = prefs.getString("${prefix}_description", "") ?: ""
            val trackingTypeStr = prefs.getString("${prefix}_trackingType", "CHECKBOX") ?: "CHECKBOX"
            val trackingType = TrackingType.valueOf(trackingTypeStr)
            val targetValue = prefs.getInt("${prefix}_targetValue", 1)
            val unit = prefs.getString("${prefix}_unit", "") ?: ""
            val createdAt = prefs.getLong("${prefix}_createdAt", System.currentTimeMillis())
            val isActive = prefs.getBoolean("${prefix}_isActive", true)

            if (id.isNotEmpty() && name.isNotEmpty()) {
                habits.add(Habit(id, name, description, trackingType, targetValue, unit, createdAt, isActive))
            }
        }

        return habits
    }

    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }

    fun updateHabit(updatedHabit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)
        // Also delete related completions
        val completions = getHabitCompletions().filter { it.habitId != habitId }
        saveHabitCompletions(completions)
    }

    // Habit Completions using XML SharedPreferences
    fun saveHabitCompletions(completions: List<HabitCompletion>) {
        val prefs = context.getSharedPreferences(COMPLETIONS_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Clear existing completions
        editor.clear()

        // Save each completion as separate key-value pairs
        editor.putInt("completions_count", completions.size)
        completions.forEachIndexed { index, completion ->
            val prefix = "completion_$index"
            editor.putString("${prefix}_habitId", completion.habitId)
            editor.putString("${prefix}_date", completion.date)
            editor.putInt("${prefix}_value", completion.value)
            editor.putLong("${prefix}_timestamp", completion.timestamp)
        }
        editor.apply()
    }

    fun getHabitCompletions(): List<HabitCompletion> {
        val prefs = context.getSharedPreferences(COMPLETIONS_PREFS, Context.MODE_PRIVATE)
        val count = prefs.getInt("completions_count", 0)
        val completions = mutableListOf<HabitCompletion>()

        for (i in 0 until count) {
            val prefix = "completion_$i"
            val habitId = prefs.getString("${prefix}_habitId", "") ?: ""
            val date = prefs.getString("${prefix}_date", "") ?: ""
            val value = prefs.getInt("${prefix}_value", 0)
            val timestamp = prefs.getLong("${prefix}_timestamp", System.currentTimeMillis())

            if (habitId.isNotEmpty() && date.isNotEmpty()) {
                completions.add(HabitCompletion(habitId, date, value, timestamp))
            }
        }

        return completions
    }

    fun addHabitCompletion(completion: HabitCompletion) {
        val completions = getHabitCompletions().toMutableList()
        // Remove existing completion for same habit and date
        completions.removeAll { it.habitId == completion.habitId && it.date == completion.date }
        completions.add(completion)
        saveHabitCompletions(completions)
    }

    fun getHabitCompletionsForDate(date: String): List<HabitCompletion> {
        return getHabitCompletions().filter { it.date == date }
    }

    fun getHabitCompletionsForWeek(startDate: String): List<HabitCompletion> {
        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormatter.parse(startDate) ?: Date()
        } catch (e: Exception) {
            calendar.time = Date()
        }

        val weekDates = mutableListOf<String>()
        repeat(7) { dayOffset ->
            weekDates.add(dateFormatter.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return getHabitCompletions().filter { completion ->
            weekDates.contains(completion.date)
        }
    }

    // Mood Entries using XML SharedPreferences
    fun saveMoodEntries(entries: List<MoodEntry>) {
        val prefs = context.getSharedPreferences(MOOD_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Clear existing entries
        editor.clear()

        // Save each mood entry as separate key-value pairs
        editor.putInt("moods_count", entries.size)
        entries.forEachIndexed { index, entry ->
            val prefix = "mood_$index"
            editor.putString("${prefix}_mood", entry.mood.name)
            editor.putString("${prefix}_notes", entry.notes)
            editor.putString("${prefix}_date", entry.date)
            editor.putLong("${prefix}_timestamp", entry.timestamp)
        }
        editor.apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val prefs = context.getSharedPreferences(MOOD_PREFS, Context.MODE_PRIVATE)
        val count = prefs.getInt("moods_count", 0)
        val entries = mutableListOf<MoodEntry>()

        for (i in 0 until count) {
            val prefix = "mood_$i"
            val moodStr = prefs.getString("${prefix}_mood", "NEUTRAL") ?: "NEUTRAL"
            val mood = Mood.valueOf(moodStr)
            val notes = prefs.getString("${prefix}_notes", "") ?: ""
            val date = prefs.getString("${prefix}_date", "") ?: ""
            val timestamp = prefs.getLong("${prefix}_timestamp", System.currentTimeMillis())

            if (date.isNotEmpty()) {
                entries.add(MoodEntry(
                    id = UUID.randomUUID().toString(),
                    mood = mood,
                    notes = notes,
                    date = date,
                    timestamp = timestamp
                ))
            }
        }

        return entries
    }

    fun addMoodEntry(entry: MoodEntry) {
        val entries = getMoodEntries().toMutableList()
        // Remove existing entry for same date
        entries.removeAll { it.date == entry.date }
        entries.add(entry)
        saveMoodEntries(entries)
    }

    fun getMoodEntryForDate(date: String): MoodEntry? {
        return getMoodEntries().find { it.date == date }
    }

    fun getMoodEntriesForWeek(startDate: Date): List<MoodEntry> {
        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.DAY_OF_YEAR, 6)
        }.time

        val startDateStr = dateFormatter.format(startDate)
        val endDateStr = dateFormatter.format(endDate)

        return getMoodEntries().filter { entry ->
            entry.date >= startDateStr && entry.date <= endDateStr
        }
    }

    // Settings using XML SharedPreferences
    fun setThemePreference(isDarkTheme: Boolean) {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("theme_dark", isDarkTheme)
        editor.apply()
    }

    fun getThemePreference(): Boolean {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean("theme_dark", false)
    }

    fun setHydrationReminders(enabled: Boolean) {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean("hydration_enabled", enabled)
        editor.apply()
    }

    fun isHydrationRemindersEnabled(): Boolean {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean("hydration_enabled", false)
    }

    fun setHydrationInterval(hours: Int) {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("hydration_interval", hours)
        editor.apply()
    }

    fun getHydrationInterval(): Int {
        val prefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        return prefs.getInt("hydration_interval", 2)
    }

    // Utility functions
    fun getTodayDate(): String {
        return dateFormatter.format(Date())
    }

    fun getCurrentWeekStart(): Date {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    // Reset all data
    fun resetAllData() {
        context.getSharedPreferences(HABITS_PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences(COMPLETIONS_PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences(MOOD_PREFS, Context.MODE_PRIVATE).edit().clear().apply()
        context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    // Test data generation for development and testing
    fun generateTestData() {
        val habits = mutableListOf<Habit>()
        val completions = mutableListOf<HabitCompletion>()
        val moodEntries = mutableListOf<MoodEntry>()

        // Create test habits
        habits.addAll(listOf(
            Habit("1", "Drink Water", "8 glasses daily", TrackingType.COUNTER, 8, "glasses"),
            Habit("2", "Morning Exercise", "30 min workout", TrackingType.CHECKBOX),
            Habit("3", "Read Books", "15 min reading", TrackingType.TIMER, 15, "minutes"),
            Habit("4", "Meditate", "Daily meditation", TrackingType.TIMER, 10, "minutes"),
            Habit("5", "Take Vitamins", "Daily supplements", TrackingType.CHECKBOX)
        ))

        // Generate past 30 days of data
        val calendar = Calendar.getInstance()
        repeat(30) { dayOffset ->
            val dateStr = dateFormatter.format(calendar.time)

            // Add habit completions (80% chance for each habit)
            habits.forEach { habit ->
                if (kotlin.random.Random.nextFloat() < 0.8f) {
                    val value = when (habit.trackingType) {
                        TrackingType.CHECKBOX -> 1
                        TrackingType.COUNTER -> kotlin.random.Random.nextInt(1, habit.targetValue + 1)
                        TrackingType.TIMER -> {
                            // Generate values in multiples of 5 minutes only
                            val increments = kotlin.random.Random.nextInt(1, (habit.targetValue / 5) + 1)
                            increments * 5
                        }
                    }
                    completions.add(HabitCompletion(habit.id, dateStr, value))
                }
            }

            // Add mood entries (90% chance each day)
            if (kotlin.random.Random.nextFloat() < 0.9f) {
                val moods = Mood.values()
                val randomMood = moods[kotlin.random.Random.nextInt(moods.size)]
                val notes = listOf(
                    "Had a great day!",
                    "Feeling productive",
                    "Nice and relaxed",
                    "Busy but good",
                    "Just okay today",
                    ""
                ).random()
                moodEntries.add(MoodEntry(
                    id = UUID.randomUUID().toString(),
                    mood = randomMood,
                    notes = notes,
                    date = dateStr
                ))
            }

            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        // Save all test data
        saveHabits(habits)
        saveHabitCompletions(completions)
        saveMoodEntries(moodEntries)
    }
}
