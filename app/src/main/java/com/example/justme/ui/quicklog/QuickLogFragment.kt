package com.example.justme.ui.quicklog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justme.data.DataManager
import com.example.justme.data.model.Habit
import com.example.justme.data.model.HabitCompletion
import com.example.justme.data.model.Mood
import com.example.justme.data.model.MoodEntry
import com.example.justme.data.model.TrackingType
import com.example.justme.databinding.FragmentQuickLogBinding
import com.example.justme.ui.mood.MoodSelectionDialogFragment

class QuickLogFragment : Fragment() {

    private var _binding: FragmentQuickLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var quickLogHabitsAdapter: QuickLogHabitsAdapter
    private val habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuickLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadHabits()
        updateTodayMoodDisplay()
    }

    private fun setupRecyclerView() {
        quickLogHabitsAdapter = QuickLogHabitsAdapter(habits) { habit, action ->
            handleHabitAction(habit, action)
        }

        binding.recyclerViewQuickHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickLogHabitsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.cardLogMood.setOnClickListener {
            showMoodSelectionDialog()
        }

        binding.btnLogMood.setOnClickListener {
            showMoodSelectionDialog()
        }
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(DataManager.getHabits().filter { it.isActive })

        if (habits.isEmpty()) {
            binding.emptyStateHabits.visibility = View.VISIBLE
            binding.recyclerViewQuickHabits.visibility = View.GONE
        } else {
            binding.emptyStateHabits.visibility = View.GONE
            binding.recyclerViewQuickHabits.visibility = View.VISIBLE
        }

        quickLogHabitsAdapter.notifyDataSetChanged()
    }

    private fun handleHabitAction(habit: Habit, action: String) {
        val today = DataManager.getTodayDate()
        val existingCompletion = DataManager.getHabitCompletionsForDate(today)
            .find { it.habitId == habit.id }

        when (action) {
            "toggle" -> {
                // Handle checkbox toggle
                if (existingCompletion == null) {
                    val completion = HabitCompletion(habit.id, today, 1)
                    DataManager.addHabitCompletion(completion)
                    showSuccessMessage("${habit.name} completed! ✅")
                } else {
                    val completions = DataManager.getHabitCompletions()
                        .filter { !(it.habitId == habit.id && it.date == today) }
                    DataManager.saveHabitCompletions(completions)
                    showSuccessMessage("${habit.name} marked as undone")
                }
            }
            "increase" -> {
                // Handle increase for counter/timer
                val currentValue = existingCompletion?.value ?: 0
                val increment = when (habit.trackingType) {
                    TrackingType.COUNTER -> 1
                    TrackingType.TIMER -> 5
                    else -> 1
                }
                val newValue = currentValue + increment
                val completion = HabitCompletion(habit.id, today, newValue)
                DataManager.addHabitCompletion(completion)

                val unit = if (habit.trackingType == TrackingType.TIMER) "minutes" else habit.unit
                showSuccessMessage("${habit.name}: +$increment $unit (Total: $newValue)")
            }
            "decrease" -> {
                // Handle decrease for counter/timer
                val currentValue = existingCompletion?.value ?: 0
                val decrement = when (habit.trackingType) {
                    TrackingType.COUNTER -> 1
                    TrackingType.TIMER -> 5
                    else -> 1
                }

                if (currentValue >= decrement) {
                    val newValue = currentValue - decrement
                    if (newValue == 0) {
                        // Remove completion entirely
                        val completions = DataManager.getHabitCompletions()
                            .filter { !(it.habitId == habit.id && it.date == today) }
                        DataManager.saveHabitCompletions(completions)
                        showSuccessMessage("${habit.name}: Reset to 0")
                    } else {
                        val completion = HabitCompletion(habit.id, today, newValue)
                        DataManager.addHabitCompletion(completion)
                        val unit = if (habit.trackingType == TrackingType.TIMER) "minutes" else habit.unit
                        showSuccessMessage("${habit.name}: -$decrement $unit (Total: $newValue)")
                    }
                }
            }
        }

        quickLogHabitsAdapter.notifyDataSetChanged()
    }

    private fun showMoodSelectionDialog() {
        val dialog = MoodSelectionDialogFragment { mood, notes ->
            saveMoodEntry(mood, notes)
        }
        dialog.show(parentFragmentManager, "mood_selection")
    }

    private fun saveMoodEntry(mood: Mood, notes: String) {
        val today = DataManager.getTodayDate()
        val moodEntry = MoodEntry(
            id = java.util.UUID.randomUUID().toString(),
            mood = mood,
            notes = notes,
            date = today
        )

        DataManager.addMoodEntry(moodEntry)
        updateTodayMoodDisplay()
        showSuccessMessage("Mood logged: ${mood.emoji} ${mood.displayName}")
    }

    private fun updateTodayMoodDisplay() {
        val today = DataManager.getTodayDate()
        val todayMood = DataManager.getMoodEntryForDate(today)

        if (todayMood != null) {
            binding.tvTodayMood.text = "${todayMood.mood.emoji} ${todayMood.mood.displayName}"
            binding.btnLogMood.text = "Update Mood"
        } else {
            binding.tvTodayMood.text = "No mood logged yet"
            binding.btnLogMood.text = "Log Mood"
        }
    }

    private fun showSuccessMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
        updateTodayMoodDisplay()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
