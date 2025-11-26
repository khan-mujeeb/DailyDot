package com.example.dailydot

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.example.dailydot.databinding.ActivityMainBinding
import com.example.dailydot.repository.HabitRepository
import com.example.dailydot.utils.HabitWorker
import com.example.dailydot.utils.Utils.getHabitCompletionImageResource
import com.example.dailydot.utils.Utils.showAddHabitDialog
import com.example.dailydot.utils.Utils.showLoaderDialog
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: HabitViewModel
    private lateinit var loader: AlertDialog

    private var habitCount: Int = 0
    private var habitByDate: Map<LocalDate, HabitData> = emptyMap()
    private lateinit var currentDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        initializeVariables()
        setupUI()
        setupClickListeners()
        scheduleHabitWorker()
    }

    private fun initializeViewModel() {
        val repository = HabitRepository(application)
        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]
    }

    private fun initializeVariables() {
        currentDate = LocalDate.now()
        loader = showLoaderDialog(this)
        loader.show()
    }

    private fun setupUI() {
        setupHabitObservers()
        setupCalendar()
        setupOnboarding()
    }

    // ------------------------------------------------------------------------
    // HABITS & TRACKING OBSERVERS
    // ------------------------------------------------------------------------
    private fun setupHabitObservers() {
        // track total count (for limiting add to 4)
        viewModel.getAllHabits().observe(this) { habits ->
            habitCount = habits?.size ?: 0
        }

        // today’s habit data for list
        viewModel.getHabitDataByDate(currentDate).observe(this) { habitData ->
            loader.dismiss()

            if (habitData != null) {
                // We already have a tracking row for today → show it
                binding.habitRcView.adapter = HabitAdapter(habitData, viewModel)
            } else {
                // No HabitData for today yet → lazily create it from Habit table
                lifecycleScope.launch {
                    viewModel.createTodayHabitDataIfMissing()
                    // After insertion, LiveData will emit again and come back into this observer
                }
                // Optionally, you can clear adapter but don't show "no data" toast anymore
                binding.habitRcView.adapter = null
            }
        }

        // all habit tracking for heatmap calendar
        viewModel.getAllHabitTrackingData().observe(this) { list ->
            habitByDate = (list ?: emptyList()).associateBy { it.date }
            binding.calendarView.notifyCalendarChanged()
        }
    }

    // ------------------------------------------------------------------------
    // CALENDAR (Kizitonwose) SETUP
    // ------------------------------------------------------------------------
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
                    container.textView.text =
                        "${data.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${data.yearMonth.year}"
                    setupDaysOfWeek(container.daysOfWeekContainer)
                }
            }
    }

    private fun setupDaysOfWeek(container: LinearLayout) {
        container.removeAllViews()
        DayOfWeek.entries.forEach { day ->
            val tv = TextView(container.context).apply {
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textSize = 12f
                setTextColor(getColor(R.color.inactive_text_color))
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
            container.addView(tv)
        }
    }

    private fun setupDayBinder() {
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                val textView = container.textView
                val habitDataForDay = habitByDate[data.date]

                val completedCount = habitDataForDay?.habitCompleted ?: 0
                val bgRes = getHabitCompletionImageResource(completedCount, textView)

                textView.apply {
                    text = data.date.dayOfMonth.toString()

                    if (data.position == DayPosition.MonthDate) {
                        setTextColor(getColor(R.color.active_text_color))
                        setBackgroundResource(if (bgRes != 0) bgRes else R.drawable.heatmap_bg0)
                    } else {
                        setTextColor(getColor(R.color.inactive_text_color))
                        setBackgroundResource(R.drawable.heatmap_bg0)
                    }

                    // show marker on today's date
                    container.markerView?.visibility =
                        if (data.date == LocalDate.now()) View.VISIBLE else View.GONE

                    setOnClickListener {
                        if (data.position == DayPosition.MonthDate && data.date <= LocalDate.now()) {
                            onDateClicked(data.date)
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid date selection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun onDateClicked(selectedDate: LocalDate) {
        val liveData = viewModel.getHabitDataByDate(selectedDate)
        liveData.observe(this) { habitData ->
            if (selectedDate == LocalDate.now()) {
                if (habitData != null) {
                    binding.habitRcView.adapter = HabitAdapter(habitData, viewModel)
                } else {
                    binding.habitRcView.adapter = null
                }
            } else {
                if (habitData != null) {
                    binding.habitRcView.adapter = PastHabitAdapter(habitData.habitStatus)
                } else {
                    binding.habitRcView.adapter = null
                    Toast.makeText(
                        this,
                        "No data available for the selected date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // one-shot observe to avoid piling observers
            liveData.removeObservers(this)
        }
    }

    // ------------------------------------------------------------------------
    // CLICKS / FAB / ONBOARDING / WORKER
    // ------------------------------------------------------------------------
    private fun setupClickListeners() {
        binding.floatingAddButton.setOnClickListener {
            if (habitCount >= 4) {
                Toast.makeText(this, "You can only add 4 habits", Toast.LENGTH_SHORT).show()
            } else {
                showAddHabitDialog(
                    context = this,
                    viewModel = viewModel,
                    lifecycleOwner = this
                )
            }
        }
    }

    private fun setupOnboarding() {
        val prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val seenOnboarding = prefs.getBoolean("hasSeenOnboarding", false)
        if (!seenOnboarding) {
            MaterialOnBoarding.setupOnBoarding(this,
                com.example.dailydot.data.OnBoardingData.getOnBoardingData(),
                object : OnFinishLastPage {
                    override fun onNext() {
                        prefs.edit().putBoolean("hasSeenOnboarding", true).apply()
                    }
                })
        }
    }

    private fun scheduleHabitWorker() {
        val currentTime = Calendar.getInstance()
        val nextExecutionTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 25)
            set(Calendar.SECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = nextExecutionTime.timeInMillis - currentTime.timeInMillis

        val request = PeriodicWorkRequestBuilder<HabitWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "HabitWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }
}
