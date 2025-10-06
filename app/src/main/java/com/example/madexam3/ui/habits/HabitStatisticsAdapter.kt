package com.example.madexam3.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Habit
import com.example.madexam3.databinding.ItemHabitStatisticsBinding
import java.text.SimpleDateFormat
import java.util.*

class HabitStatisticsAdapter(
    private val habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitStatisticsAdapter.StatisticsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        val binding = ItemHabitStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatisticsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    inner class StatisticsViewHolder(private val binding: ItemHabitStatisticsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun calculateCurrentStreak(habit: Habit): Int {
            val completions = DataManager.getHabitCompletions().filter { it.habitId == habit.id }
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            var streak = 0

            // Check backwards from today
            for (i in 0..30) { // Check last 30 days max
                val dateStr = dateFormatter.format(calendar.time)
                val dayCompletion = completions.find { it.date == dateStr }
                val completionValue = dayCompletion?.value ?: 0

                // Only count as streak if target was fully reached (green status)
                val targetReached = when (habit.trackingType) {
                    com.example.madexam3.data.model.TrackingType.CHECKBOX -> completionValue == 1
                    com.example.madexam3.data.model.TrackingType.COUNTER,
                    com.example.madexam3.data.model.TrackingType.TIMER -> completionValue >= habit.targetValue
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

        private fun getCurrentWeekData(habit: Habit): List<CompletionStatus> {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val completions = DataManager.getHabitCompletions().filter { it.habitId == habit.id }
            val today = DataManager.getTodayDate()

            // Get current week start (Monday)
            val weekStart = DataManager.getCurrentWeekStart()
            val calendar = Calendar.getInstance()
            calendar.time = weekStart

            val weekData = mutableListOf<CompletionStatus>()

            // Generate Monday to Sunday data for current week
            repeat(7) {
                val dateStr = dateFormatter.format(calendar.time)
                val dayCompletion = completions.find { it.date == dateStr }
                val completionValue = dayCompletion?.value ?: 0

                val status = when {
                    dateStr == today -> {
                        // Today's special logic
                        when (habit.trackingType) {
                            com.example.madexam3.data.model.TrackingType.CHECKBOX -> {
                                if (completionValue == 1) CompletionStatus.COMPLETED
                                else CompletionStatus.TODAY_PENDING
                            }
                            com.example.madexam3.data.model.TrackingType.COUNTER,
                            com.example.madexam3.data.model.TrackingType.TIMER -> {
                                when {
                                    completionValue >= habit.targetValue -> CompletionStatus.COMPLETED
                                    completionValue > 0 -> CompletionStatus.PARTIAL
                                    else -> CompletionStatus.TODAY_PENDING
                                }
                            }
                        }
                    }
                    dateStr > today -> {
                        // Future days should always be grey
                        CompletionStatus.FUTURE_DAY
                    }
                    else -> {
                        // Past days logic
                        when (habit.trackingType) {
                            com.example.madexam3.data.model.TrackingType.CHECKBOX -> {
                                if (completionValue == 1) CompletionStatus.COMPLETED
                                else CompletionStatus.NOT_COMPLETED
                            }
                            com.example.madexam3.data.model.TrackingType.COUNTER,
                            com.example.madexam3.data.model.TrackingType.TIMER -> {
                                when {
                                    completionValue >= habit.targetValue -> CompletionStatus.COMPLETED
                                    completionValue > 0 -> CompletionStatus.PARTIAL
                                    else -> CompletionStatus.NOT_COMPLETED
                                }
                            }
                        }
                    }
                }

                weekData.add(status)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return weekData
        }

        private fun updateDayViews(completions: List<CompletionStatus>) {
            val dayViews = listOf(
                binding.dayMon, binding.dayTue, binding.dayWed,
                binding.dayThu, binding.dayFri, binding.daySat, binding.daySun
            )

            val context = binding.root.context
            val completedColor = context.getColor(android.R.color.holo_green_dark)
            val partialColor = context.getColor(android.R.color.holo_orange_light)
            val notCompletedColor = context.getColor(android.R.color.holo_red_light)
            val todayPendingColor = context.getColor(android.R.color.darker_gray)

            completions.forEachIndexed { index, status ->
                val color = when (status) {
                    CompletionStatus.COMPLETED -> completedColor
                    CompletionStatus.PARTIAL -> partialColor
                    CompletionStatus.NOT_COMPLETED -> notCompletedColor
                    CompletionStatus.TODAY_PENDING -> todayPendingColor
                    CompletionStatus.FUTURE_DAY -> todayPendingColor // Future days also grey
                }

                dayViews[index].backgroundTintList = android.content.res.ColorStateList.valueOf(color)
            }
        }

        private fun calculateTotalActiveDays(habit: Habit): Int {
            val completions = DataManager.getHabitCompletions().filter { it.habitId == habit.id }

            // For counter and timer habits, only count days where target was reached
            val successfulDays = when (habit.trackingType) {
                com.example.madexam3.data.model.TrackingType.CHECKBOX -> {
                    // For checkbox habits, count unique dates with completions (value = 1)
                    completions.filter { it.value == 1 }.map { it.date }.distinct().size
                }
                com.example.madexam3.data.model.TrackingType.COUNTER,
                com.example.madexam3.data.model.TrackingType.TIMER -> {
                    // For counter/timer habits, only count days where target was reached
                    completions.filter { it.value >= habit.targetValue }.map { it.date }.distinct().size
                }
            }

            // Calculate days since creation as backup
            val creationDate = Date(habit.createdAt)
            val today = Date()
            val diffInMillis = today.time - creationDate.time
            val daysSinceCreation = (diffInMillis / (24 * 60 * 60 * 1000)).toInt() + 1

            // For new habits with no successful days yet, show days since creation
            // For habits with activity, show successful days count
            return if (successfulDays > 0) successfulDays else daysSinceCreation.coerceAtMost(1)
        }

        private fun calculateCurrentWeekSuccessRate(habit: Habit): Int {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val weekStart = DataManager.getCurrentWeekStart()
            val weekStartStr = dateFormatter.format(weekStart)

            // Get current week completions
            val weekCompletions = DataManager.getHabitCompletionsForWeek(weekStartStr)
                .filter { it.habitId == habit.id }

            val calendar = Calendar.getInstance()
            calendar.time = weekStart
            var successfulDays = 0

            // Check each day in current week up to today
            val today = DataManager.getTodayDate()
            repeat(7) { dayOffset ->
                val checkDate = dateFormatter.format(calendar.time)

                // Only count days up to today (don't count future days)
                if (checkDate <= today) {
                    val completion = weekCompletions.find { it.date == checkDate }

                    // Check if the habit was successfully completed on this day
                    val wasSuccessful = when (habit.trackingType) {
                        com.example.madexam3.data.model.TrackingType.CHECKBOX -> completion?.value == 1
                        com.example.madexam3.data.model.TrackingType.COUNTER,
                        com.example.madexam3.data.model.TrackingType.TIMER ->
                            (completion?.value ?: 0) >= habit.targetValue
                    }

                    if (wasSuccessful) {
                        successfulDays++
                    }
                }
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Always calculate success rate based on 7 days for consistency
            // This gives more meaningful percentages throughout the week
            return (successfulDays * 100) / 7
        }

        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name
                tvHabitDescription.text = habit.description

                // Calculate current streak
                val currentStreak = calculateCurrentStreak(habit)
                tvStreakNumber.text = currentStreak.toString()

                // Get current week data (Monday to Sunday)
                val currentWeekData = getCurrentWeekData(habit)
                updateDayViews(currentWeekData)

                // Calculate weekly progress (only count fully completed days in current week)
                val weeklyCompletions = currentWeekData.count { it == CompletionStatus.COMPLETED }
                tvWeeklyProgress.text = "$weeklyCompletions/7"

                // Calculate success rate for current week based on target achievement
                val successRate = calculateCurrentWeekSuccessRate(habit)
                tvCompletionRate.text = "$successRate%"

                // Calculate total days since habit creation
                val totalDays = calculateTotalActiveDays(habit)
                tvTotalDays.text = totalDays.toString()

                // Handle item click
                root.setOnClickListener {
                    onHabitClick(habit)
                }
            }
        }
    }

    enum class CompletionStatus {
        COMPLETED,      // Green - target fully reached
        PARTIAL,        // Yellow - some progress but target not reached (counter/timer only)
        NOT_COMPLETED,  // Red - no progress
        TODAY_PENDING,  // Grey - today but not yet completed
        FUTURE_DAY      // Grey - future days
    }
}
