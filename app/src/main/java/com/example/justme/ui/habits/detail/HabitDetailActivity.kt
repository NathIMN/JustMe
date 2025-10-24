package com.example.justme.ui.habits.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justme.data.DataManager
import com.example.justme.data.model.Habit
import com.example.justme.data.model.HabitCompletion
import com.example.justme.databinding.ActivityHabitDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitDetailBinding
    private lateinit var habit: Habit
    private lateinit var historyAdapter: HabitHistoryAdapter
    private val historyItems = mutableListOf<HabitHistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val habitId = intent.getStringExtra("habit_id")
        if (habitId == null) {
            finish()
            return
        }

        val foundHabit = DataManager.getHabits().find { it.id == habitId }
        if (foundHabit == null) {
            finish()
            return
        }

        habit = foundHabit

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        loadHabitData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh habit data when returning from edit activity
        val updatedHabit = DataManager.getHabits().find { it.id == habit.id }
        if (updatedHabit != null) {
            habit = updatedHabit
            binding.toolbar.title = habit.name // Update toolbar title too
            loadHabitData()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.title = habit.name
    }

    private fun setupRecyclerView() {
        historyAdapter = HabitHistoryAdapter(historyItems)
        binding.recyclerViewHistory.apply {
            layoutManager = LinearLayoutManager(this@HabitDetailActivity)
            adapter = historyAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnEditHabit.setOnClickListener {
            val intent = Intent(this, com.example.justme.ui.habits.edit.EditHabitActivity::class.java)
            intent.putExtra("habit_id", habit.id)
            startActivity(intent)
        }

        binding.btnDeleteHabit.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun loadHabitData() {
        binding.apply {
            tvHabitName.text = habit.name
            tvHabitDescription.text = habit.description

            // Show tracking type info
            val trackingInfo = when (habit.trackingType) {
                com.example.justme.data.model.TrackingType.CHECKBOX -> "Simple completion tracking"
                com.example.justme.data.model.TrackingType.COUNTER -> "Target: ${habit.targetValue} ${habit.unit}"
                com.example.justme.data.model.TrackingType.TIMER -> "Target: ${habit.targetValue} minutes"
            }
            tvTrackingInfo.text = trackingInfo

            // Show creation date
            val creationDate = Date(habit.createdAt)
            val creationFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            tvCreatedDate.text = "Created on ${creationFormatter.format(creationDate)}"

            // Load statistics
            loadStatistics()
            loadHistory()
        }
    }

    private fun loadStatistics() {
        val completions = DataManager.getHabitCompletions().filter { it.habitId == habit.id }

        // Calculate total days where target was reached (not just attempted)
        val totalDays = when (habit.trackingType) {
            com.example.justme.data.model.TrackingType.CHECKBOX -> {
                // For checkbox habits, count unique dates with completions (value = 1)
                val successfulDays = completions.filter { it.value == 1 }.map { it.date }.distinct().size
                // If no successful days yet, show days since creation
                if (successfulDays > 0) {
                    successfulDays
                } else {
                    val creationDate = Date(habit.createdAt)
                    val today = Date()
                    val diffInMillis = today.time - creationDate.time
                    ((diffInMillis / (24 * 60 * 60 * 1000)).toInt() + 1).coerceAtMost(1)
                }
            }
            com.example.justme.data.model.TrackingType.COUNTER,
            com.example.justme.data.model.TrackingType.TIMER -> {
                // For counter/timer habits, only count days where target was reached
                val successfulDays = completions.filter { it.value >= habit.targetValue }.map { it.date }.distinct().size
                // If no successful days yet, show days since creation
                if (successfulDays > 0) {
                    successfulDays
                } else {
                    val creationDate = Date(habit.createdAt)
                    val today = Date()
                    val diffInMillis = today.time - creationDate.time
                    ((diffInMillis / (24 * 60 * 60 * 1000)).toInt() + 1).coerceAtMost(1)
                }
            }
        }

        // Calculate current streak
        val currentStreak = calculateCurrentStreak(completions)

        // Calculate this week's progress
        val weekStart = DataManager.getCurrentWeekStart()
        val weekStartStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(weekStart)
        val weekCompletions = DataManager.getHabitCompletionsForWeek(weekStartStr)
            .filter { it.habitId == habit.id }
        val weekProgress = weekCompletions.size

        binding.apply {
            tvTotalDays.text = "$totalDays days"
            tvCurrentStreak.text = "$currentStreak days"
            tvWeekProgress.text = "$weekProgress/7 days"

            // Update progress bar
            progressWeekly.progress = (weekProgress / 7.0f * 100).toInt()
        }
    }

    private fun calculateCurrentStreak(completions: List<HabitCompletion>): Int {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var streak = 0

        // Check backwards from today
        for (i in 0..30) { // Check last 30 days max
            val dateStr = dateFormatter.format(calendar.time)
            val dayCompletion = completions.find { it.date == dateStr }
            val completionValue = dayCompletion?.value ?: 0

            // Only count as streak if target was fully reached (same logic as fragment)
            val targetReached = when (habit.trackingType) {
                com.example.justme.data.model.TrackingType.CHECKBOX -> completionValue == 1
                com.example.justme.data.model.TrackingType.COUNTER,
                com.example.justme.data.model.TrackingType.TIMER -> completionValue >= habit.targetValue
            }

            if (targetReached) {
                streak++
            } else if (i > 0) { // Don't break on first day (today) if no completions
                break
            }
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }

    private fun loadHistory() {
        historyItems.clear()
        val completions = DataManager.getHabitCompletions()
            .filter { it.habitId == habit.id }

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val displayFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        // Sort completions by actual date, not string
        val sortedCompletions = completions.sortedByDescending { completion ->
            try {
                dateFormatter.parse(completion.date)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }

        for (completion in sortedCompletions) {
            try {
                val date = dateFormatter.parse(completion.date)
                val displayDate = if (date != null) displayFormatter.format(date) else completion.date

                val valueText = when (habit.trackingType) {
                    com.example.justme.data.model.TrackingType.CHECKBOX -> "Completed"
                    com.example.justme.data.model.TrackingType.COUNTER -> "${completion.value} ${habit.unit}"
                    com.example.justme.data.model.TrackingType.TIMER -> "${completion.value} minutes"
                }

                historyItems.add(HabitHistoryItem(displayDate, valueText))
            } catch (e: Exception) {
                // Skip invalid dates
            }
        }

        if (historyItems.isEmpty()) {
            binding.tvEmptyHistory.visibility = android.view.View.VISIBLE
            binding.recyclerViewHistory.visibility = android.view.View.GONE
        } else {
            binding.tvEmptyHistory.visibility = android.view.View.GONE
            binding.recyclerViewHistory.visibility = android.view.View.VISIBLE
        }

        historyAdapter.notifyDataSetChanged()
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"? This will also delete all completion history.")
            .setPositiveButton("Delete") { _, _ ->
                deleteHabit()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteHabit() {
        DataManager.deleteHabit(habit.id)
        Toast.makeText(this, "Habit deleted", Toast.LENGTH_SHORT).show()
        finish()
    }
}

data class HabitHistoryItem(
    val date: String,
    val value: String
)
