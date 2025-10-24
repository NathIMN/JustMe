package com.example.justme.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.justme.data.DataManager
import com.example.justme.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()
    }

    private fun loadProfileData() {
        // Display dummy profile data
        binding.apply {
            tvProfileName.text = "Nimsara Nath"
            tvProfileEmail.text = "nimsaranath@gmail.com"

            // Calculate statistics
            val habits = DataManager.getHabits()
            val activeHabits = habits.filter { it.isActive }
            val moodEntries = DataManager.getMoodEntries()

            // Total habits
            tvTotalHabitsValue.text = activeHabits.size.toString()

            // Calculate current streak (consecutive days with at least one habit completed)
            val currentStreak = calculateCurrentStreak()
            tvStreakValue.text = currentStreak.toString()

            // Total mood entries
            tvMoodEntriesValue.text = moodEntries.size.toString()

            // This week's completions
            val weekStart = DataManager.getCurrentWeekStart()
            val weekStartStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(weekStart)
            val weekCompletions = DataManager.getHabitCompletionsForWeek(weekStartStr)
            val uniqueDaysThisWeek = weekCompletions.map { it.date }.distinct().size
            tvWeeklyCompletionsValue.text = "$uniqueDaysThisWeek/7 days"

            // Best habit (most completed)
            val bestHabit = findBestHabit()
            tvBestHabitValue.text = bestHabit ?: "No habits yet"

            // Favorite mood (most frequent)
            val favoriteMood = findFavoriteMood()
            tvFavoriteMoodValue.text = favoriteMood ?: "No moods logged"
        }
    }

    private fun calculateCurrentStreak(): Int {
        val calendar = Calendar.getInstance()
        var streak = 0

        // Check backwards from today
        for (i in 0..30) { // Check last 30 days max
            val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            val dayCompletions = DataManager.getHabitCompletionsForDate(dateStr)

            if (dayCompletions.isNotEmpty()) {
                streak++
            } else if (i > 0) { // Don't break on first day (today) if no completions
                break
            }

            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }

    private fun findBestHabit(): String? {
        val habits = DataManager.getHabits()
        val completions = DataManager.getHabitCompletions()

        val habitCompletionCounts = habits.map { habit ->
            val count = completions.count { it.habitId == habit.id }
            habit.name to count
        }.maxByOrNull { it.second }

        return if (habitCompletionCounts != null && habitCompletionCounts.second > 0) {
            "${habitCompletionCounts.first} (${habitCompletionCounts.second} times)"
        } else null
    }

    private fun findFavoriteMood(): String? {
        val moodEntries = DataManager.getMoodEntries()

        val moodCounts = moodEntries.groupBy { it.mood }
            .mapValues { it.value.size }
            .maxByOrNull { it.value }

        return if (moodCounts != null) {
            "${moodCounts.key.emoji} ${moodCounts.key.displayName} (${moodCounts.value} times)"
        } else null
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
