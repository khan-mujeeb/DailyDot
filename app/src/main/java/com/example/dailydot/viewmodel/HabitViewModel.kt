package com.example.dailydot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.repository.HabitRepository
import com.example.dailydot.utils.Callback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun getHabitsByDate(date: LocalDate, callback: Callback<LiveData<HabitData>>) {

        CoroutineScope(Dispatchers.IO).launch {

            withContext(Dispatchers.Main) {
                callback.onResult(repository.getHabitsByDate(date))
            }
        }
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

}
