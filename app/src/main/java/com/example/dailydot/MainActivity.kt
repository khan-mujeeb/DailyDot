package com.example.dailydot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dailydot.adapter.DayViewContainer
import com.example.dailydot.adapter.HabitAdapter
import com.example.dailydot.adapter.MonthViewContainer
import com.example.dailydot.adapter.PastHabitAdapter
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.data.OnBoardingData.getOnBoardingData
import com.example.dailydot.databinding.ActivityMainBinding
import com.example.dailydot.repository.HabitRepository
import com.example.dailydot.utils.HabitWorker
import com.example.dailydot.utils.Utils.getHabitCompletionImageResource
import com.example.dailydot.utils.Utils.showAddHabitDialog
import com.example.dailydot.utils.Utils.showEditDeleteHabitPopup
import com.example.dailydot.viewmodel.HabitViewModel
import com.example.dailydot.viewmodel.HabitViewModelFactory
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.xcode.onboarding.MaterialOnBoarding
import com.xcode.onboarding.OnFinishLastPage
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HabitViewModel
    private var habitFlag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVariables()
        setupUI()
        subscribeOnClickEvents()
        scheduleHabitWorker()


        insertDummyDataIfFirstTime()

    }

    private fun subscribeOnClickEvents() {
        binding.floatingAddButton.setOnClickListener {
            if (habitFlag >= 4) {
                Toast.makeText(this, "You can only add 4 habits", Toast.LENGTH_SHORT).show()
            } else {
                showAddHabitDialog(context = this, viewModel = viewModel, lifecycleOwner = this)
            }
        }
    }

    private fun initializeVariables() {
        val repository = HabitRepository(application)
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]
    }

    private fun setupUI() {
        setupHabitListObserver()
        setupCalendar()
        setupOnboarding()


    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        binding.calendarView.setup(
            currentMonth.minusMonths(12),
            currentMonth,
            DayOfWeek.MONDAY
        )
        binding.calendarView.scrollToMonth(currentMonth)

        setupMonthHeaderBinder()
        setupDayBinder()
    }

    private fun setupMonthHeaderBinder() {
        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View): MonthViewContainer = MonthViewContainer(view)

                @SuppressLint("SetTextI18n")
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    container.textView.text = "${
                        data.yearMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${data.yearMonth.year}"
                    setupDaysOfWeek(container.daysOfWeekContainer)
                }
            }
    }

    private fun setupDaysOfWeek(container: LinearLayout) {
        container.removeAllViews()
        DayOfWeek.entries.forEach { day ->
            val dayTextView = TextView(container.context).apply {
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textSize = 12f
                setTextColor(Color.GRAY)
                gravity = Gravity.CENTER
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            container.addView(dayTextView)
        }
    }

    private fun setupDayBinder() {
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {

                // Show marker for the current date
                if (data.date == LocalDate.now()) {
                    container.markerView.visibility = View.VISIBLE
                } else {
                    container.markerView.visibility = View.GONE
                }

                container.textView.apply {
                    text = data.date.dayOfMonth.toString()
                    setTextColor(getColor(if (data.position == DayPosition.MonthDate) R.color.active_text_color else R.color.inactive_text_color))


                    // Set click listener for each date
                    setOnClickListener {
                        if (data.position == DayPosition.MonthDate && data.date <= LocalDate.now()) {
                            onDateClicked(data)
                        } else {
                            Toast.makeText(context, "Invalid date selection", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                observeHabitTracking(container, data, container.textView)
            }
        }
    }


    private fun onDateClicked(data: CalendarDay) {
        // Handle date click action
        val selectedDate = data.date

        loadHabitsForSelectedDate(selectedDate)

    }

    private fun loadHabitsForSelectedDate(selectedDate: LocalDate) {
        lifecycleScope.launch {
            val pastHabitList = viewModel.getHabitsByDate(selectedDate)

            pastHabitList.observe(this@MainActivity) { habitData ->
                if (selectedDate == LocalDate.now()) {
                    setupHabitListObserver()

                } else {
                    if (habitData != null) {
                        binding.habitRcView.adapter = PastHabitAdapter(habitData.habitStatus)
                    } else {
                        binding.habitRcView.adapter = null
                        Toast.makeText(
                            this@MainActivity,
                            "No data available for the selected date",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                pastHabitList.removeObservers(this@MainActivity)

            }
        }
    }


    // **************** set habit resource based on of habit completed or not *********************
    private fun observeHabitTracking(
        container: DayViewContainer,
        data: CalendarDay,
        textView: TextView
    ) {

        lifecycleScope.launch {
            viewModel.getAllHabitTrackingData()
                .observe(this@MainActivity) { habitDataList ->
                    val habitData = habitDataList.find { it.date == data.date }
                    container.textView.setBackgroundResource(
                        habitData?.let {
                            getHabitCompletionImageResource(
                                it.habitCompleted,
                                textView
                            )
                        }
                            ?: 0 // Default background if no habit data is found
                    )
                }
        }
    }


    //********************************* fetch Habit list from db  ********************************

    private fun setupHabitListObserver() {


        lifecycleScope.launch {


            lifecycleScope.launch {
                viewModel.getHabitsByDate(LocalDate.now())
                    .observe(this@MainActivity) { habitData ->

                        viewModel.getAllHabits().observe(this@MainActivity) { habits ->
                            if (habitData != null) {
                                habitFlag = habits.size
                                binding.habitRcView.adapter = HabitAdapter(
                                    lifecycleOwner = this@MainActivity,
                                    habits = habits,
                                    habitData = habitData,
                                    viewModel = viewModel,

                                    ) { habit, actionType, x, y ->

                                    showEditDeleteHabitPopup(
                                        binding,
                                        context = this@MainActivity,
                                        viewModel = viewModel,
                                        habit = habit,
                                        x,
                                        y
                                    )
                                }
                            } else {
                                binding.habitRcView.adapter = null
                            }
                        }
                    }


            }
        }
    }


    //********************************* on boarding screens for first time user ********************************
    private fun setupOnboarding() {
        MaterialOnBoarding.setupOnBoarding(
            this,
            getOnBoardingData(),
            object : OnFinishLastPage {
                override fun onNext() {

                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    finish()
                }
            })
    }


//    ********************************* Worker for daily resting the habit tracking data ********************************

    private fun scheduleHabitWorker() {
        val currentTime = Calendar.getInstance()
        val nextExecutionTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 25)
            set(Calendar.SECOND, 0)

            // If the time has already passed for today, schedule it for tomorrow
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = nextExecutionTime.timeInMillis - currentTime.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<HabitWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "HabitWorker", // A unique name to prevent duplicate workers
            ExistingPeriodicWorkPolicy.REPLACE, // Replace the existing worker if any
            workRequest
        )
    }


    fun insertDummyDataIfFirstTime() {
        val sharedPreferences: SharedPreferences = this@MainActivity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Check if dummy data is already inserted
        val isDataInserted = sharedPreferences.getBoolean("isDummyDataInserted", false)

        if (!isDataInserted) {
            // Insert dummy data
            val startDate = LocalDate.of(2024, 1, 1)
            val endDate = LocalDate.of(2024, 12, 10)
            var currentDate = startDate

            while (!currentDate.isAfter(endDate)) {
                // Generate a randomized list of HabitStatus
                val sampleHabits = listOf(
                    HabitStatus("97-11effaad-1b8a-486d-b594-ca6092008de0", "a", Random.nextBoolean()),
                    HabitStatus("98-b424e3ec-9386-4836-96b3-af3846c930a5", "b", Random.nextBoolean()),
                    HabitStatus("99-ff218df0-d41d-4427-98bc-eb75b04f4a3b", "c", Random.nextBoolean()),
                    HabitStatus("100-ce5d5c28-b416-424b-bc7d-bac1d872f6c9", "d", Random.nextBoolean()),
                    HabitStatus("3154295-d555125b-c7e8-4e97-811c-5119c70b2d46", "fuck", Random.nextBoolean())
                )

                // Count the number of habits marked as true
                val habitCompleted = sampleHabits.count { it.habitStatus }

                // Create a HabitData object
                val habitData = HabitData(
                    date = currentDate,
                    habitStatus = sampleHabits,
                    habitCompleted = habitCompleted
                )

                // Insert the data into the database using ViewModel
                viewModel.insertHabitData(habitData)

                // Move to the next day
                currentDate = currentDate.plusDays(1)
            }

            // Mark data as inserted in SharedPreferences
            sharedPreferences.edit().putBoolean("isDummyDataInserted", true).apply()
        }
    }
}
