package com.example.madexam3.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Mood
import com.example.madexam3.data.model.MoodEntry
import com.example.madexam3.databinding.FragmentMoodBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var moodCalendarAdapter: MoodCalendarAdapter
    private val calendarDays = mutableListOf<CalendarDay>()
    private var currentCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()
        setupMoodChart()
        setupClickListeners()
        loadMoodData()
    }

    private fun setupCalendar() {
        moodCalendarAdapter = MoodCalendarAdapter(calendarDays) { day ->
            // Handle day click - can show mood details
        }

        binding.recyclerViewCalendar.apply {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = moodCalendarAdapter
        }

        generateCalendarDays()
        updateMonthDisplay()
    }

    private fun generateCalendarDays() {
        calendarDays.clear()

        // Get the month and year we're currently viewing
        val viewingMonth = currentCalendar.get(Calendar.MONTH)
        val viewingYear = currentCalendar.get(Calendar.YEAR)

        // Create a calendar for the first day of the viewing month
        val monthCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, viewingYear)
            set(Calendar.MONTH, viewingMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK)

        // Add empty days for proper alignment (Monday = 1, Sunday = 7)
        // Adjust for Monday as first day of week
        val adjustedFirstDay = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
        for (i in 0 until adjustedFirstDay) {
            calendarDays.add(CalendarDay(0, null, false))
        }

        // Add all days of the viewing month
        val maxDays = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDays) {
            monthCalendar.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(monthCalendar.time)
            val moodEntry = DataManager.getMoodEntryForDate(dateStr)

            // Check if this day is today
            val today = Calendar.getInstance()
            val isToday = monthCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    monthCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    monthCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

            calendarDays.add(CalendarDay(day, moodEntry?.mood, isToday))
        }

        moodCalendarAdapter.notifyDataSetChanged()
    }

    private fun updateMonthDisplay() {
        val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding.tvCurrentMonth.text = monthFormatter.format(currentCalendar.time)
    }

    private fun setupMoodChart() {
        val chart = binding.chartMoodTrend

        // Get mood entries for the past week
        val weekStart = DataManager.getCurrentWeekStart()
        val moodEntries = DataManager.getMoodEntriesForWeek(weekStart)

        val entries = mutableListOf<Entry>()
        val calendar = Calendar.getInstance()
        calendar.time = weekStart
        val today = DataManager.getTodayDate()

        for (i in 0..6) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            // Only add entries for dates up to today (don't show future dates)
            if (dateStr <= today) {
                val moodEntry = moodEntries.find { it.date == dateStr }
                val moodValue = moodEntry?.mood?.value?.toFloat() ?: 0f // Past dates without mood = 0
                entries.add(Entry(i.toFloat(), moodValue))
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val dataSet = LineDataSet(entries, "Weekly Mood Trend")
        dataSet.apply {
            color = resources.getColor(android.R.color.holo_blue_bright, null)
            setCircleColor(resources.getColor(android.R.color.holo_blue_bright, null))
            lineWidth = 3f
            circleRadius = 6f
            setDrawValues(false)
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 5f
            invalidate()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnPreviousMonth.setOnClickListener {
                currentCalendar.add(Calendar.MONTH, -1)
                generateCalendarDays()
                updateMonthDisplay()
            }

            btnNextMonth.setOnClickListener {
                currentCalendar.add(Calendar.MONTH, 1)
                generateCalendarDays()
                updateMonthDisplay()
            }

            btnLogTodayMood.setOnClickListener {
                showMoodSelectionDialog()
            }
        }
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
            id = UUID.randomUUID().toString(),
            mood = mood,
            notes = notes,
            date = today
        )

        DataManager.addMoodEntry(moodEntry)

        // Refresh all mood-related displays immediately
        updateTodayMoodDisplay()
        generateCalendarDays() // Refresh calendar to show new mood
        setupMoodChart() // Refresh chart

        android.widget.Toast.makeText(requireContext(), "Mood logged: ${mood.emoji}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun updateTodayMoodDisplay() {
        // Update today's mood display
        val today = DataManager.getTodayDate()
        val todayMood = DataManager.getMoodEntryForDate(today)

        if (todayMood != null) {
            binding.tvTodayMood.text = "${todayMood.mood.emoji} ${todayMood.mood.displayName}"
            binding.btnLogTodayMood.text = "Update Today's Mood"
        } else {
            binding.tvTodayMood.text = "How are you feeling today?"
            binding.btnLogTodayMood.text = "Log Today's Mood"
        }
    }

    private fun loadMoodData() {
        generateCalendarDays()
        setupMoodChart()
        updateTodayMoodDisplay()
    }

    override fun onResume() {
        super.onResume()
        loadMoodData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class CalendarDay(
    val day: Int,
    val mood: Mood?,
    val isToday: Boolean
)
