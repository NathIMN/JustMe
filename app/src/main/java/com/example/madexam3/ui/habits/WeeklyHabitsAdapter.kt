package com.example.madexam3.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madexam3.R
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Habit
import com.example.madexam3.databinding.ItemHabitWeeklyBinding
import java.text.SimpleDateFormat
import java.util.*

class WeeklyHabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<WeeklyHabitsAdapter.WeeklyHabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyHabitViewHolder {
        val binding = ItemHabitWeeklyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeeklyHabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyHabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    inner class WeeklyHabitViewHolder(private val binding: ItemHabitWeeklyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name

                // Get current week start (Monday)
                val weekStart = DataManager.getCurrentWeekStart()
                val weekStartStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(weekStart)
                val calendar = Calendar.getInstance()
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                try {
                    calendar.time = weekStart
                } catch (e: Exception) {
                    calendar.time = Date()
                }

                // Get all completions for this habit for the current week
                val weekCompletions = DataManager.getHabitCompletionsForWeek(weekStartStr)
                    .filter { it.habitId == habit.id }

                // Set colors for each day based on completion
                val dayViews = listOf(dayMon, dayTue, dayWed, dayThu, dayFri, daySat, daySun)
                val completedColor = root.context.getColor(android.R.color.holo_green_dark)
                val notCompletedColor = root.context.getColor(android.R.color.darker_gray)

                for (i in 0..6) {
                    val dayDate = dateFormatter.format(calendar.time)
                    val isCompleted = weekCompletions.any { it.date == dayDate }

                    dayViews[i].backgroundTintList = android.content.res.ColorStateList.valueOf(
                        if (isCompleted) completedColor else notCompletedColor
                    )

                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                root.setOnClickListener {
                    onHabitClick(habit)
                }
            }
        }
    }
}
