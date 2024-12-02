package com.example.dailydot.repository

import androidx.lifecycle.LiveData
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.database.HabitDao
import com.example.dailydot.database.HabitDataDao
import java.time.LocalDate

class HabitRepository(private val habitDao: HabitDao, private val habitDataDao: HabitDataDao) {


//    **********************************************************************************************
//                                          habit tracking  Data
//    **********************************************************************************************
//    fun getHabitsByDate(date: LocalDate): LiveData<List<HabitData>> {
//        return habitDataDao.getHabitsByDate(date)
//    }


//    suspend fun insertHabitData(habitData: HabitData) {
//        habitDataDao.insertHabitData(habitData)
//    }



//    **********************************************************************************************
//                                          habit Data
//    **********************************************************************************************
    suspend fun insertHabit(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit) {
        habitDao.deleteHabit(habit)
    }

    suspend fun getAllHabits(): List<Habit> {
        return habitDao.getAllHabits()
    }
}
