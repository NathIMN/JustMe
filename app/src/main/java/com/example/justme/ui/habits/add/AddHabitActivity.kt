package com.example.justme.ui.habits.add

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.justme.MainActivity
import com.example.justme.R
import com.example.justme.data.DataManager
import com.example.justme.data.model.Habit
import com.example.justme.data.model.TrackingType
import com.example.justme.databinding.ActivityAddHabitBinding

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private var selectedTrackingType = TrackingType.CHECKBOX

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide bottom navigation from MainActivity
        (this as? MainActivity)?.hideBottomNavigation()

        setupToolbar()
        setupTrackingTypeSpinner()
        setupClickListeners()
        updateUIForTrackingType()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTrackingTypeSpinner() {
        val trackingTypes = arrayOf(
            getString(R.string.tracking_checkbox),
            getString(R.string.tracking_counter),
            getString(R.string.tracking_timer)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, trackingTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTrackingType.adapter = adapter

        binding.spinnerTrackingType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedTrackingType = when (position) {
                    0 -> TrackingType.CHECKBOX
                    1 -> TrackingType.COUNTER
                    2 -> TrackingType.TIMER
                    else -> TrackingType.CHECKBOX
                }
                updateUIForTrackingType()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
    }

    private fun setupClickListeners() {
        binding.btnSaveHabit.setOnClickListener {
            saveHabit()
        }
    }

    private fun updateUIForTrackingType() {
        when (selectedTrackingType) {
            TrackingType.CHECKBOX -> {
                binding.layoutTarget.visibility = android.view.View.GONE
                binding.layoutUnit.visibility = android.view.View.GONE
            }
            TrackingType.COUNTER -> {
                binding.layoutTarget.visibility = android.view.View.VISIBLE
                binding.layoutUnit.visibility = android.view.View.VISIBLE
                binding.etTarget.hint = "Target count (e.g., 8)"
                binding.etUnit.hint = "Unit (e.g., cups, times)"
            }
            TrackingType.TIMER -> {
                binding.layoutTarget.visibility = android.view.View.VISIBLE
                binding.layoutUnit.visibility = android.view.View.GONE
                binding.etTarget.hint = "Target minutes (e.g., 30)"
            }
        }
    }

    private fun saveHabit() {
        val name = binding.etHabitName.text.toString().trim()
        val description = binding.etHabitDescription.text.toString().trim()
        val targetText = binding.etTarget.text.toString().trim()
        val unit = binding.etUnit.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.etHabitName.error = "Habit name is required"
            return
        }

        var targetValue = 1
        if (selectedTrackingType != TrackingType.CHECKBOX) {
            if (targetText.isEmpty()) {
                binding.etTarget.error = "Target is required"
                return
            }

            targetValue = targetText.toIntOrNull() ?: 1
            if (targetValue <= 0) {
                binding.etTarget.error = "Target must be greater than 0"
                return
            }
        }

        if (selectedTrackingType == TrackingType.COUNTER && unit.isEmpty()) {
            binding.etUnit.error = "Unit is required for counter tracking"
            return
        }

        // Create and save habit
        val habit = Habit(
            name = name,
            description = description,
            trackingType = selectedTrackingType,
            targetValue = targetValue,
            unit = if (selectedTrackingType == TrackingType.COUNTER) unit else ""
        )

        DataManager.addHabit(habit)

        Toast.makeText(this, "Habit saved successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Show bottom navigation when leaving this activity
        (this as? MainActivity)?.showBottomNavigation()
    }
}
