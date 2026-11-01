package com.example.dailydot.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.database.HabitDatabase
import java.time.LocalDate

class HabitRepository(application: Application) {

    val applicationContext = application.applicationContext
    private val db = HabitDatabase.getDatabase(application)
    private val habitDao = db.habitDao()
    private val habitDataDao = db.habitDataDao()

    // Habit list

//    suspend fun updateHabit(habit: Habit) =
//        habitDao.updateHabit(habit.uid, habit.habitName, habit.habitIcon, habit.habitColor)

    suspend fun insertHabit(habit: Habit) = habitDao.insertHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    fun getAllHabits(): LiveData<List<Habit>> = habitDao.getAllHabits()
    suspend fun updateHabit(habit: Habit) =
        habitDao.updateHabit(
            habit.uid,
            habit.habitName,
            habit.habitIcon,
            habit.habitColor,
            habit.goalTarget,
            habit.goalUnit
        )
    suspend fun getAllHabitsList(): List<Habit> = habitDao.getAllHabitsList()


    // Habit tracking data
    suspend fun insertHabitData(habitData: HabitData) = habitDataDao.insertHabitData(habitData)

    suspend fun updateHabitData(
        date: LocalDate,
        habitStatus: List<HabitStatus>,
        habitCompleted: Int
    ) = habitDataDao.updateHabitData(date, habitStatus, habitCompleted)

    fun getHabitDataByDate(date: LocalDate): LiveData<HabitData?> =
        habitDataDao.getHabitDataByDate(date)

    fun getAllHabitTrackingData(): LiveData<List<HabitData>> =
        habitDataDao.getAllHabitData()

    fun getHabitTrackingDataBetween(
        startDate: LocalDate,
        endDate: LocalDate
    ): LiveData<List<HabitData>> =
        habitDataDao.getHabitDataBetween(startDate, endDate)

    suspend fun getOnceHabitsByDate(date: LocalDate): HabitData? =
        habitDataDao.getOnceHabitsByDate(date)


}
