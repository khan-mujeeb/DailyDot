package com.example.dailydot.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.database.HabitDao
import com.example.dailydot.database.HabitDataDao
import com.example.dailydot.database.HabitDatabase
import java.time.LocalDate

class HabitRepository(application: Application) {

    private val habitDao = HabitDatabase.getDatabase(application).habitDao()
    private val habitDataDao = HabitDatabase.getDatabase(application).habitDataDao()


//    **********************************************************************************************
//                                          habit tracking  Data
//    **********************************************************************************************
//    fun getHabitsByDate(date: LocalDate): LiveData<List<HabitData>> {
//        return habitDataDao.getHabitsByDate(date)
//    }


    suspend fun insertHabitData(habitData: HabitData) {
        habitDataDao.insertHabitData(habitData)
    }

    suspend fun updateHabitData(date: LocalDate, habitStatus: List<HabitStatus>, habitCompleted: Int) {
        habitDataDao.updateHabitData(date, habitStatus, habitCompleted)
    }

    suspend fun getHabitsByDate(date: LocalDate): LiveData<HabitData> {
        return habitDataDao.getHabitsByDate(date)
    }

    fun getAllHabitTrackingData(): LiveData<List<HabitData>> {
        return habitDataDao.getAllHabits()
    }



//    **********************************************************************************************
//                                          habit Data
//    **********************************************************************************************
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    fun getAllHabits(): LiveData<List<Habit>> {
        return habitDao.getAllHabits()
    }
}
