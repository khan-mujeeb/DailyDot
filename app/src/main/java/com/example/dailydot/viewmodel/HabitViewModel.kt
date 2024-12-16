package com.example.dailydot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate


class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

//    **********************************************************************************************
//                                          habit tracking  Data
//    **********************************************************************************************


    fun insertHabitData(habitData: HabitData) {
        viewModelScope.launch {
            repository.insertHabitData(habitData)
        }
    }

    fun updateHabitData(date: LocalDate, habitStatus: List<HabitStatus>, habitCompleted: Int) {
        viewModelScope.launch {
            repository.updateHabitData(date, habitStatus, habitCompleted)
        }
    }

    suspend fun getHabitsByDate(date: LocalDate): LiveData<HabitData> {


        return repository.getHabitsByDate(date)
    }


    fun getAllHabitTrackingData(): LiveData<List<HabitData>> {
        return repository.getAllHabitTrackingData()
    }


    //    **********************************************************************************************
//                                          habit Data
//    **********************************************************************************************
    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun getAllHabits(): LiveData<List<Habit>> {
        return repository.getAllHabits()
    }


    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    suspend fun getOnceHabitsByDate(date: LocalDate): HabitData {
        return repository.getOnceHabitsByDate(date)
    }


    //    **********************************************************************************************
    //                                          adapter Data
    //    **********************************************************************************************
    fun addHabitToCompleted(habit: Habit, result: HabitData?) {
        val updatedStatus = result?.habitStatus?.map {
            if (it.uid == habit.uid) {
                it.copy(habitStatus = true) // Update the habit's status
            } else {
                it
            }
        }?.toMutableList() ?: mutableListOf()


        updateHabitData(
            LocalDate.now(),
            updatedStatus,
            (result?.habitCompleted ?: 0) + 1
        )

    }


    fun removeHabitFromCompleted(habit: Habit, result: HabitData?) {
        val updatedStatus = result?.habitStatus?.map {
            if (it.uid == habit.uid) {
                it.copy(habitStatus = false) // Update the habit's status
            } else {
                it
            }
        }?.toMutableList() ?: mutableListOf()




        updateHabitData(
            LocalDate.now(),
            updatedStatus,
            (result?.habitCompleted ?: 1) - 1
        )

    }


    fun addNewHabitSetupData(habit: Habit, result: HabitData?) {


        val updatedStatus = result?.habitStatus?.toMutableList() // Convert to mutable list

        // Add the new HabitStatus to the list


        if (result == null) {

            val tempHabit = HabitData(
                id = 0,
                date = LocalDate.now(),
                habitStatus = listOf(
                    HabitStatus(
                        uid = habit.uid,
                        habitStatus = false,
                        habitName = habit.habitName
                    )
                ),

                habitCompleted = 0

            )

            insertHabitData(tempHabit)

        } else {

            updatedStatus?.add(
                HabitStatus(
                    uid = habit.uid,
                    habitStatus = false,
                    habitName = habit.habitName
                )
            )

            updateHabitData(
                date = LocalDate.now(),
                habitStatus = updatedStatus!!,
                habitCompleted = result.habitCompleted
            )
        }


    }

}
