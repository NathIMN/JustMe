package com.example.madexam3.ui.habits.edit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Habit
import com.example.madexam3.databinding.ActivityEditHabitBinding

class EditHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditHabitBinding
    private lateinit var habit: Habit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditHabitBinding.inflate(layoutInflater)
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
        setupViews()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.toolbar.title = "Edit ${habit.name}"
    }

    private fun setupViews() {
        binding.apply {
            etDescription.setText(habit.description)

            // Show current habit info that can't be edited
            tvHabitName.text = habit.name
            tvTrackingType.text = habit.trackingType.name.lowercase().replaceFirstChar { it.uppercase() }

            // Show unit and target value only for COUNTER and TIMER types
            when (habit.trackingType) {
                com.example.madexam3.data.model.TrackingType.CHECKBOX -> {
                    // Hide unit info and target value fields for checkbox habits
                    layoutUnitInfo.visibility = android.view.View.GONE
                    layoutTargetValue.visibility = android.view.View.GONE
                }
                com.example.madexam3.data.model.TrackingType.COUNTER,
                com.example.madexam3.data.model.TrackingType.TIMER -> {
                    // Show unit and target value fields
                    layoutUnitInfo.visibility = android.view.View.VISIBLE
                    layoutTargetValue.visibility = android.view.View.VISIBLE
                    etTargetValue.setText(habit.targetValue.toString())
                    tvUnit.text = habit.unit.ifEmpty {
                        if (habit.trackingType == com.example.madexam3.data.model.TrackingType.TIMER) "minutes" else "N/A"
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveChanges()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveChanges() {
        val newDescription = binding.etDescription.text.toString().trim()

        if (newDescription.isEmpty()) {
            binding.etDescription.error = "Description cannot be empty"
            return
        }

        // Only validate target value for non-checkbox habits
        val newTargetValue = when (habit.trackingType) {
            com.example.madexam3.data.model.TrackingType.CHECKBOX -> habit.targetValue // Keep existing value
            else -> {
                val targetValueStr = binding.etTargetValue.text.toString().trim()
                try {
                    val value = targetValueStr.toInt()
                    if (value <= 0) {
                        binding.etTargetValue.error = "Target value must be greater than 0"
                        return
                    }
                    value
                } catch (e: NumberFormatException) {
                    binding.etTargetValue.error = "Please enter a valid number"
                    return
                }
            }
        }

        // Create updated habit
        val updatedHabit = habit.copy(
            description = newDescription,
            targetValue = newTargetValue
        )

        // Update in data manager
        val habits = DataManager.getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            DataManager.saveHabits(habits)

            Toast.makeText(this, "Habit updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error updating habit", Toast.LENGTH_SHORT).show()
        }
    }
}
