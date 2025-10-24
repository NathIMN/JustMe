package com.example.justme.ui.habits.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justme.databinding.ItemHabitHistoryBinding

class HabitHistoryAdapter(
    private val historyItems: List<HabitHistoryItem>
) : RecyclerView.Adapter<HabitHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHabitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyItems[position])
    }

    override fun getItemCount() = historyItems.size

    inner class HistoryViewHolder(private val binding: ItemHabitHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HabitHistoryItem) {
            binding.apply {
                tvDate.text = item.date
                tvValue.text = item.value
            }
        }
    }
}
