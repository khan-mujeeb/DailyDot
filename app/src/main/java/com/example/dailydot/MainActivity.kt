package com.example.dailydot

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.dailydot.adapter.DayViewContainer
import com.example.dailydot.adapter.HabitAdapter
import com.example.dailydot.adapter.MonthViewContainer
import com.example.dailydot.data.Habit
import com.example.dailydot.databinding.ActivityMainBinding
import com.example.dailydot.repository.HabitRepository
import com.example.dailydot.utils.Utils.generateRandomUID
import com.example.dailydot.viewmodel.HabitViewModel
import com.example.dailydot.viewmodel.HabitViewModelFactory
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HabitViewModel
    private var habitFlag: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInitialization()
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
//        val habitStatusMap = habitDataList.associateBy { it.date }

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
//                val habitData = habitStatusMap[data.date]
//
//                val completedActivities = habitDataList
//                    .filter { it.date == data.date && it.status }
//                    .size

//                val backgroundResource = when (completedActivities) {
//                    0 -> R.drawable.heatmap_bg0 // No activities
//                    1 -> R.drawable.heatmap_bg1 // 1 activity completed
//                    2 -> R.drawable.heatmap_bg2 // 2 activities completed
//                    3 -> R.drawable.heatmap_bg3 // 3 activities completed
//                    4 -> R.drawable.heatmap_bg4 // 4 activities completed
//                    else -> R.drawable.heatmap_bg4 // Default to max
//                }

//                textView.setBackgroundResource(backgroundResource)
            }
        }
    }

    private fun variableInitialization() {
        val repository = HabitRepository(application)
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

    }


    private fun subscribeOnClickListner() {

        binding.floatingAddButton.setOnClickListener {
            if(habitFlag == 4) {
                Toast.makeText(this, "You can only add 4 habits", Toast.LENGTH_SHORT).show()
            } else {
                showAddHabitDialog()
            }
        }

    }

    private fun subscribeUI() {

        getHabitList()
    }

    private fun getHabitList() {
        lifecycleScope.launch {

            viewModel.getAllHabits().observe(this@MainActivity) {
                if (it.isNotEmpty()) {
                    binding.habitRcView.adapter = HabitAdapter(it)
                }

                habitFlag = it.size
            }


        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAddHabitDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val btnAddHabit = dialogView.findViewById<Button>(R.id.btnAddHabit)

        // Create and show the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnAddHabit.setOnClickListener {
            val habitName = etHabitName.text.toString().trim()

            if (habitName.isNotEmpty()) {

                // Add the habit to the database using ViewModel
                val newHabit = Habit(
                    uid = generateRandomUID(habitName),
                    habitName = habitName
                )

                viewModel.insertHabit(newHabit)

                // Dismiss the dialog
                dialog.dismiss()
            } else {
                etHabitName.error = "Habit name cannot be empty"
            }
        }

        dialog.show()
    }
}

