package com.example.dailydot

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.dailydot.adapter.DayViewContainer
import com.example.dailydot.adapter.MonthViewContainer
import com.example.dailydot.data.DummyData.habitDataList
import com.example.dailydot.databinding.ActivityMainBinding
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
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


        val currentMonth = YearMonth.now()
        binding.calendarView.setup(currentMonth.minusMonths(1), currentMonth.plusMonths(1), DayOfWeek.SUNDAY)
        binding.calendarView.scrollToMonth(currentMonth)

        // Map habit data to dates
        val habitStatusMap = habitDataList.associateBy { it.date }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View): MonthViewContainer {
                return MonthViewContainer(view)
            }

            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Format the month and year
                val monthName = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                val year = month.yearMonth.year
                container.textView.text = "$monthName $year" // e.g., "November 2024"
            }
        }

        binding.calendarView.setup(currentMonth.minusMonths(12), currentMonth.plusMonths(12), DayOfWeek.SUNDAY)
        binding.calendarView.scrollToMonth(currentMonth)


        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.WHITE)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }

                // Check habit status
                val habitData = habitStatusMap[data.date]
                when (habitData?.status) {
                    true -> {
                        textView.setBackgroundResource(R.drawable.circle_green) // Completed habit
                    }
                    false -> {
                        textView.setBackgroundResource(R.drawable.circle_red) // Missed habit
                    }
                    else -> {
                        textView.background = null // No data for this day
                    }
                }
            }
        }


    }
}

