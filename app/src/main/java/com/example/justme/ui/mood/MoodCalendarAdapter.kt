package com.example.justme.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justme.databinding.ItemCalendarDayBinding

class MoodCalendarAdapter(
    private val days: List<CalendarDay>,
    private val onDayClick: (CalendarDay) -> Unit
) : RecyclerView.Adapter<MoodCalendarAdapter.CalendarDayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount() = days.size

    inner class CalendarDayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            binding.apply {
                if (day.day == 0) {
                    // Empty day for alignment
                    tvDay.text = ""
                    tvMoodEmoji.text = ""
                    root.isClickable = false
                    root.alpha = 0f
                } else {
                    tvDay.text = day.day.toString()
                    tvMoodEmoji.text = day.mood?.emoji ?: ""
                    root.isClickable = true
                    root.alpha = 1f

                    // Highlight today
                    if (day.isToday) {
                        root.setBackgroundColor(root.context.getColor(android.R.color.holo_blue_light))
                        tvDay.setTextColor(root.context.getColor(android.R.color.white))
                    } else {
                        root.background = null
                        // Use theme-appropriate text color
                        val textColor = root.context.getColor(android.R.color.black)
                        tvDay.setTextColor(textColor)
                    }

                    root.setOnClickListener {
                        onDayClick(day)
                    }
                }
            }
        }
    }
}
