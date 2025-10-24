package com.example.justme.ui.mood

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.justme.data.model.Mood
import com.example.justme.databinding.DialogMoodSelectionBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MoodSelectionDialogFragment(
    private val onMoodSelected: (Mood, String) -> Unit
) : DialogFragment() {

    private var _binding: DialogMoodSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var moodOptionsAdapter: MoodOptionsAdapter
    private var selectedMood: Mood? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMoodSelectionBinding.inflate(layoutInflater)

        setupMoodOptions()
        setupClickListeners()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle("How are you feeling?")
            .setPositiveButton("Save") { _, _ ->
                selectedMood?.let { mood ->
                    val notes = binding.etNotes.text.toString()
                    onMoodSelected(mood, notes)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun setupMoodOptions() {
        val moods = Mood.values().toList()
        moodOptionsAdapter = MoodOptionsAdapter(moods) { mood ->
            selectedMood = mood
            binding.tvSelectedMood.text = "${mood.emoji} ${mood.displayName}"
            binding.tvSelectedMood.visibility = View.VISIBLE
        }

        binding.recyclerViewMoods.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = moodOptionsAdapter
        }
    }

    private fun setupClickListeners() {
        // No additional click listeners needed for now
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
