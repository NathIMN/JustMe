package com.example.madexam3.ui.habits

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.Habit
import com.example.madexam3.databinding.FragmentHabitsBinding
import com.example.madexam3.ui.habits.add.AddHabitActivity
import com.example.madexam3.ui.habits.detail.HabitDetailActivity

class HabitsFragment : Fragment() {

    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitStatisticsAdapter: HabitStatisticsAdapter
    private val habits = mutableListOf<Habit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }

    private fun setupRecyclerView() {
        habitStatisticsAdapter = HabitStatisticsAdapter(
            habits = habits,
            onHabitClick = { habit ->
                val intent = Intent(requireContext(), HabitDetailActivity::class.java)
                intent.putExtra("habit_id", habit.id)
                startActivity(intent)
            }
        )

        binding.recyclerViewHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitStatisticsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            startActivity(Intent(requireContext(), AddHabitActivity::class.java))
        }
    }

    private fun loadHabits() {
        habits.clear()
        habits.addAll(DataManager.getHabits().filter { it.isActive })

        if (habits.isEmpty()) {
            binding.emptyStateView.visibility = View.VISIBLE
            binding.recyclerViewHabits.visibility = View.GONE
        } else {
            binding.emptyStateView.visibility = View.GONE
            binding.recyclerViewHabits.visibility = View.VISIBLE
        }

        habitStatisticsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadHabits()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
