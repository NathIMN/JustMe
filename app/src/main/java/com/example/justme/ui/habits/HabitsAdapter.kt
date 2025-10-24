package com.example.justme.ui.habits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justme.data.DataManager
import com.example.justme.data.model.Habit
import com.example.justme.data.model.HabitCompletion
import com.example.justme.data.model.TrackingType
import com.example.justme.databinding.ItemHabitBinding

class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitClick: (Habit) -> Unit,
    private val onHabitToggle: (Habit, Boolean) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    inner class HabitViewHolder(private val binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name
                tvHabitDescription.text = habit.description

                // Check if habit is completed today
                val today = DataManager.getTodayDate()
                val todayCompletion = DataManager.getHabitCompletionsForDate(today)
                    .find { it.habitId == habit.id }

                val isCompleted = todayCompletion != null

                when (habit.trackingType) {
                    TrackingType.CHECKBOX -> {
                        checkboxCompletion.visibility = android.view.View.VISIBLE
                        tvCounterValue.visibility = android.view.View.GONE
                        btnIncrement.visibility = android.view.View.GONE
                        btnDecrement.visibility = android.view.View.GONE

                        checkboxCompletion.isChecked = isCompleted
                        checkboxCompletion.setOnCheckedChangeListener { _, checked ->
                            onHabitToggle(habit, checked)
                        }
                    }

                    TrackingType.COUNTER -> {
                        checkboxCompletion.visibility = android.view.View.GONE
                        tvCounterValue.visibility = android.view.View.VISIBLE
                        btnIncrement.visibility = android.view.View.VISIBLE
                        btnDecrement.visibility = android.view.View.VISIBLE

                        val currentValue = todayCompletion?.value ?: 0
                        tvCounterValue.text = "$currentValue / ${habit.targetValue} ${habit.unit}"

                        btnIncrement.setOnClickListener {
                            val newValue = currentValue + 1
                            val completion = HabitCompletion(habit.id, today, newValue)
                            DataManager.addHabitCompletion(completion)
                            notifyItemChanged(bindingAdapterPosition)
                        }

                        btnDecrement.setOnClickListener {
                            if (currentValue > 0) {
                                val newValue = currentValue - 1
                                if (newValue == 0) {
                                    // Remove completion
                                    val completions = DataManager.getHabitCompletions()
                                        .filter { !(it.habitId == habit.id && it.date == today) }
                                    DataManager.saveHabitCompletions(completions)
                                } else {
                                    val completion = HabitCompletion(habit.id, today, newValue)
                                    DataManager.addHabitCompletion(completion)
                                }
                                notifyItemChanged(bindingAdapterPosition)
                            }
                        }
                    }

                    TrackingType.TIMER -> {
                        checkboxCompletion.visibility = android.view.View.GONE
                        tvCounterValue.visibility = android.view.View.VISIBLE
                        btnIncrement.visibility = android.view.View.GONE
                        btnDecrement.visibility = android.view.View.GONE

                        val currentValue = todayCompletion?.value ?: 0
                        tvCounterValue.text = "$currentValue / ${habit.targetValue} minutes"
                    }
                }

                // Set completion status styling
                root.alpha = if (isCompleted && habit.trackingType == TrackingType.CHECKBOX) 0.7f else 1.0f

                // Handle item click
                root.setOnClickListener {
                    onHabitClick(habit)
                }

                // Show weekly progress
                updateWeeklyProgress(habit)
            }
        }

        private fun updateWeeklyProgress(habit: Habit) {
            val weekStart = DataManager.getCurrentWeekStart()
            val weekStartStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(weekStart)
            val weekCompletions = DataManager.getHabitCompletionsForWeek(weekStartStr)
                .filter { it.habitId == habit.id }

            val daysCompleted = weekCompletions.size
            val progressPercentage = (daysCompleted / 7.0f * 100).toInt()

            binding.progressWeekly.progress = progressPercentage
            binding.tvWeeklyProgress.text = "$daysCompleted/7 days"
        }
    }
}
