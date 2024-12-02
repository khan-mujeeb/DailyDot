package com.example.dailydot

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydot.adapter.DayViewContainer
import com.example.dailydot.adapter.HabitAdapter
import com.example.dailydot.adapter.MonthViewContainer
import com.example.dailydot.data.DummyData.habbit
import com.example.dailydot.data.DummyData.habitDataList
import com.example.dailydot.databinding.ActivityMainBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeUI()
        subscribeOnClickListner()

        val currentMonth = YearMonth.now()

        // Make sure to set the correct start day for the calendar
        binding.calendarView.setup(
            currentMonth.minusMonths(12), // Previous month
            currentMonth.plusMonths(0),   // Next month
            DayOfWeek.MONDAY              // Starting day of the week as Monday
        )
        binding.calendarView.scrollToMonth(currentMonth)

        // Map habit data to dates
        val habitStatusMap = habitDataList.associateBy { it.date }

        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View): MonthViewContainer {
                    return MonthViewContainer(view)
                }

                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Set the month and year
                    val monthName =
                        data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    val year = data.yearMonth.year
                    container.textView.text = "$monthName $year" // e.g., "November 2024"

                    // Add days of the week to the header (Sunday to Saturday)
                    val daysOfWeek = DayOfWeek.values() // Sunday to Saturday
                    container.daysOfWeekContainer.removeAllViews() // Clear any existing views

                    for (day in daysOfWeek) {
                        val dayTextView = TextView(container.daysOfWeekContainer.context).apply {
                            text = day.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            ) // e.g., Sun, Mon
                            textSize = 12f
                            setTextColor(Color.GRAY)
                            gravity = android.view.Gravity.CENTER
                            layoutParams = LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                            )
                        }
                        container.daysOfWeekContainer.addView(dayTextView)
                    }
                }
            }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(getColor(R.color.active_text_color))
                } else {
                    container.textView.setTextColor(getColor(R.color.inactive_text_color))
                }

                // Check habit status and map background resources
                val habitData = habitStatusMap[data.date]

                val completedActivities = habitDataList
                    .filter { it.date == data.date && it.status }
                    .size

                val backgroundResource = when (completedActivities) {
                    0 -> R.drawable.heatmap_bg0 // No activities
                    1 -> R.drawable.heatmap_bg1 // 1 activity completed
                    2 -> R.drawable.heatmap_bg2 // 2 activities completed
                    3 -> R.drawable.heatmap_bg3 // 3 activities completed
                    4 -> R.drawable.heatmap_bg4 // 4 activities completed
                    else -> R.drawable.heatmap_bg4 // Default to max
                }

                textView.setBackgroundResource(backgroundResource)
            }
        }
    }



    private fun subscribeOnClickListner() {

        binding.floatingAddButton.setOnClickListener {
            // Open the add habit activity
        }

    }
    private fun subscribeUI() {
        binding.habitRcView.adapter = HabitAdapter(habbit)
    }
}

