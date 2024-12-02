package com.example.dailydot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

//    fun getHabitsByDate(date: LocalDate): LiveData<List<HabitData>> {
//        return repository.getHabitsByDate(date)
//    }

    fun insertHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

//    fun insertHabitData(habitData: HabitData) {
//        viewModelScope.launch {
//            repository.insertHabitData(habitData)
//        }
//    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    suspend fun getAllHabits(): List<Habit> {
        return repository.getAllHabits()
    }
}
