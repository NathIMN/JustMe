package com.example.madexam3.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.madexam3.MainActivity
import com.example.madexam3.R
import com.example.madexam3.data.DataManager
import com.example.madexam3.data.model.TrackingType

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widget instances
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Initialize DataManager when first widget is created
        DataManager.init(context)
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Initialize DataManager
            DataManager.init(context)

            // Get active habits and today's completions
            val habits = DataManager.getHabits().filter { it.isActive }
            val today = DataManager.getTodayDate()
            val todayCompletions = DataManager.getHabitCompletionsForDate(today)

            // Create RemoteViews for the widget layout
            val views = RemoteViews(context.packageName, R.layout.widget_habit_completion)

            // Clear any existing habit views
            views.removeAllViews(R.id.habitsList)

            if (habits.isEmpty()) {
                // Show "No habits" message
                val noHabitsView = RemoteViews(context.packageName, R.layout.widget_habit_item)
                noHabitsView.setTextViewText(R.id.tvHabitName, "No habits today")
                noHabitsView.setTextViewText(R.id.tvHabitStatus, "Add habits to get started!")
                noHabitsView.setTextColor(R.id.tvHabitStatus, Color.parseColor("#3E2723"))
                views.addView(R.id.habitsList, noHabitsView)
            } else {
                // Add each habit as a separate view
                habits.take(5).forEach { habit -> // Limit to 5 habits for widget space
                    val habitView = RemoteViews(context.packageName, R.layout.widget_habit_item)

                    // Set habit name
                    habitView.setTextViewText(R.id.tvHabitName, habit.name)

                    // Get today's completion for this habit
                    val completion = todayCompletions.find { it.habitId == habit.id }

                    // Set status based on tracking type - using only black and #3E2723
                    val statusText = when (habit.trackingType) {
                        TrackingType.CHECKBOX -> {
                            if (completion != null) {
                                "✅ Done"
                            } else {
                                "⏳ Pending"
                            }
                        }
                        TrackingType.COUNTER -> {
                            val currentValue = completion?.value ?: 0
                            "$currentValue/${habit.targetValue} ${habit.unit}"
                        }
                        TrackingType.TIMER -> {
                            val currentValue = completion?.value ?: 0
                            "$currentValue/${habit.targetValue} min"
                        }
                    }

                    habitView.setTextViewText(R.id.tvHabitStatus, statusText)
                    habitView.setTextColor(R.id.tvHabitStatus, Color.parseColor("#3E2723"))

                    views.addView(R.id.habitsList, habitView)
                }

                // If there are more than 5 habits, show a "more" indicator
                if (habits.size > 5) {
                    val moreView = RemoteViews(context.packageName, R.layout.widget_habit_item)
                    moreView.setTextViewText(R.id.tvHabitName, "...")
                    moreView.setTextViewText(R.id.tvHabitStatus, "+${habits.size - 5} more")
                    moreView.setTextColor(R.id.tvHabitStatus, Color.parseColor("#3E2723"))
                    views.addView(R.id.habitsList, moreView)
                }
            }

            // Calculate overall completion percentage for the title
            val completionPercentage = if (habits.isNotEmpty()) {
                var completedHabits = 0
                habits.forEach { habit ->
                    val completion = todayCompletions.find { it.habitId == habit.id }
                    val completionValue = completion?.value ?: 0

                    val isCompleted = when (habit.trackingType) {
                        TrackingType.CHECKBOX -> completionValue == 1
                        TrackingType.COUNTER, TrackingType.TIMER -> completionValue >= habit.targetValue
                    }

                    if (isCompleted) {
                        completedHabits++
                    }
                }
                (completedHabits * 100) / habits.size
            } else {
                0
            }

            views.setTextViewText(R.id.tvWidgetTitle, "Today's Habits ($completionPercentage%)")

            // Create intent to open the app when widget is tapped
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, HabitWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val widgetManager = AppWidgetManager.getInstance(context)
            val ids = widgetManager.getAppWidgetIds(
                android.content.ComponentName(context, HabitWidgetProvider::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
