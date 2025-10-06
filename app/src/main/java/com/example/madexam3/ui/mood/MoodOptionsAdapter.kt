package com.example.madexam3.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madexam3.data.model.Mood
import com.example.madexam3.databinding.ItemMoodOptionBinding

class MoodOptionsAdapter(
    private val moods: List<Mood>,
    private val onMoodSelected: (Mood) -> Unit
) : RecyclerView.Adapter<MoodOptionsAdapter.MoodOptionViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodOptionViewHolder {
        val binding = ItemMoodOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodOptionViewHolder, position: Int) {
        holder.bind(moods[position], position == selectedPosition)
    }

    override fun getItemCount() = moods.size

    inner class MoodOptionViewHolder(private val binding: ItemMoodOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mood: Mood, isSelected: Boolean) {
            binding.apply {
                tvMoodEmoji.text = mood.emoji
                tvMoodName.text = mood.displayName

                // Update selection state
                root.isSelected = isSelected
                if (isSelected) {
                    root.setBackgroundColor(root.context.getColor(android.R.color.holo_blue_light))
                } else {
                    root.setBackgroundColor(root.context.getColor(android.R.color.transparent))
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = bindingAdapterPosition

                    // Notify changes for selection update
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(selectedPosition)

                    onMoodSelected(mood)
                }
            }
        }
    }
}
