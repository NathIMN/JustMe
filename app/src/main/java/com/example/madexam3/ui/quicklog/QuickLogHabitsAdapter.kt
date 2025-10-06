package com.example.madexam3.ui.quicklog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Habit
import com.example.madexam3.data.model.TrackingType
import com.example.madexam3.databinding.ItemQuickHabitBinding

class QuickLogHabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitAction: (Habit, String) -> Unit // Changed to include action type
) : RecyclerView.Adapter<QuickLogHabitsAdapter.QuickHabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickHabitViewHolder {
        val binding = ItemQuickHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuickHabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickHabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount() = habits.size

    inner class QuickHabitViewHolder(private val binding: ItemQuickHabitBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                tvHabitName.text = habit.name

                // Get today's completion status
                val today = DataManager.getTodayDate()
                val todayCompletion = DataManager.getHabitCompletionsForDate(today)
                    .find { it.habitId == habit.id }

                // Update status and buttons based on tracking type
                when (habit.trackingType) {
                    TrackingType.CHECKBOX -> {
                        // Show single toggle button for checkbox habits
                        btnQuickLog.visibility = android.view.View.VISIBLE
                        layoutCounterButtons.visibility = android.view.View.GONE

                        if (todayCompletion != null) {
                            tvStatus.text = "✅ Completed"
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
                            btnQuickLog.text = "Mark Undone"
                        } else {
                            tvStatus.text = "⏳ Pending"
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_orange_dark))
                            btnQuickLog.text = "Mark Done"
                        }

                        btnQuickLog.setOnClickListener {
                            onHabitAction(habit, "toggle")
                        }
                    }
                    TrackingType.COUNTER -> {
                        // Show separate + and - buttons for counter habits
                        btnQuickLog.visibility = android.view.View.GONE
                        layoutCounterButtons.visibility = android.view.View.VISIBLE

                        val currentValue = todayCompletion?.value ?: 0
                        tvStatus.text = "$currentValue / ${habit.targetValue} ${habit.unit}"
                        if (currentValue >= habit.targetValue) {
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
                        } else {
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_orange_dark))
                        }

                        // Set button text for counter (+1/-1)
                        btnIncrease.text = "+1"
                        btnDecrease.text = "-1"

                        // Enable/disable decrease button based on current value
                        btnDecrease.isEnabled = currentValue > 0
                        btnDecrease.alpha = if (currentValue > 0) 1.0f else 0.5f

                        btnIncrease.setOnClickListener {
                            onHabitAction(habit, "increase")
                        }

                        btnDecrease.setOnClickListener {
                            onHabitAction(habit, "decrease")
                        }
                    }
                    TrackingType.TIMER -> {
                        // Show separate + and - buttons for timer habits
                        btnQuickLog.visibility = android.view.View.GONE
                        layoutCounterButtons.visibility = android.view.View.VISIBLE

                        val currentValue = todayCompletion?.value ?: 0
                        tvStatus.text = "$currentValue / ${habit.targetValue} minutes"
                        if (currentValue >= habit.targetValue) {
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
                        } else {
                            tvStatus.setTextColor(root.context.getColor(android.R.color.holo_orange_dark))
                        }

                        // Set button text for timer (+5/-5)
                        btnIncrease.text = "+5"
                        btnDecrease.text = "-5"

                        // Enable/disable decrease button based on current value
                        btnDecrease.isEnabled = currentValue >= 5
                        btnDecrease.alpha = if (currentValue >= 5) 1.0f else 0.5f

                        btnIncrease.setOnClickListener {
                            onHabitAction(habit, "increase")
                        }

                        btnDecrease.setOnClickListener {
                            onHabitAction(habit, "decrease")
                        }
                    }
                }
            }
        }
    }
}
