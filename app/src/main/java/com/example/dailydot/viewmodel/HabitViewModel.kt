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
import kotlin.math.max

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    // Habits
    fun insertHabit(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }

    fun getAllHabits(): LiveData<List<Habit>> = repository.getAllHabits()

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repository.deleteHabit(habit)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repository.updateHabit(habit)
    }

    // HabitData
    fun insertHabitData(habitData: HabitData) = viewModelScope.launch {
        repository.insertHabitData(habitData)
    }

    fun updateHabitData(date: LocalDate, habitStatus: List<HabitStatus>, habitCompleted: Int) {
        viewModelScope.launch {
            repository.updateHabitData(date, habitStatus, habitCompleted)
        }
    }

    fun getHabitDataByDate(date: LocalDate): LiveData<HabitData?> =
        repository.getHabitDataByDate(date)

    fun getAllHabitTrackingData(): LiveData<List<HabitData>> =
        repository.getAllHabitTrackingData()

    suspend fun getOnceHabitsByDate(date: LocalDate): HabitData? =
        repository.getOnceHabitsByDate(date)

    // checkbox helpers
    fun addHabitToCompleted(habit: HabitStatus, result: HabitData) {
        val updatedStatus = result.habitStatus.map {
            if (it.uid == habit.uid) it.copy(habitStatus = true) else it
        }
        val newCompleted = result.habitCompleted + 1
        updateHabitData(result.date, updatedStatus, newCompleted)
    }

    fun removeHabitFromCompleted(habit: HabitStatus, result: HabitData) {
        val updatedStatus = result.habitStatus.map {
            if (it.uid == habit.uid) it.copy(habitStatus = false) else it
        }
        val newCompleted = max(result.habitCompleted - 1, 0)
        updateHabitData(result.date, updatedStatus, newCompleted)
    }

    // in HabitViewModel.kt

    suspend fun ensureTodayHabitDataIncludes(newHabit: Habit) {
        val today = LocalDate.now()

        // Is there already HabitData for today?
        val existing: HabitData? = repository.getOnceHabitsByDate(today)

        if (existing == null) {
            // No row yet: create a fresh HabitData from ALL current habits
            val allHabits = repository.getAllHabitsList()
            if (allHabits.isEmpty()) return

            val statuses = allHabits.map {
                HabitStatus(
                    uid = it.uid,
                    habitName = it.habitName,
                    habitStatus = false
                )
            }

            val habitData = HabitData(
                date = today,
                habitStatus = statuses,
                habitCompleted = 0
            )
            repository.insertHabitData(habitData)
        } else {
            // Row exists: just add new habit to status list if not already present
            val updatedStatuses = existing.habitStatus.toMutableList()

            val alreadyPresent = updatedStatuses.any { it.uid == newHabit.uid }
            if (!alreadyPresent) {
                updatedStatuses.add(
                    HabitStatus(
                        uid = newHabit.uid,
                        habitName = newHabit.habitName,
                        habitStatus = false
                    )
                )

                repository.updateHabitData(
                    date = today,
                    habitStatus = updatedStatuses,
                    habitCompleted = existing.habitCompleted
                )
            }
        }
    }

    suspend fun createTodayHabitDataIfMissing() {
        val today = LocalDate.now()

        // Check if there is already HabitData for today
        val existing: HabitData? = repository.getOnceHabitsByDate(today)
        if (existing != null) return  // already created, nothing to do

        // Fetch all habits
        val allHabits: List<Habit> = repository.getAllHabitsList()
        if (allHabits.isEmpty()) return // no habits yet, so no tracking row needed

        // Build status list: all habits, all unchecked
        val statuses = allHabits.map { habit ->
            HabitStatus(
                uid = habit.uid,
                habitName = habit.habitName,
                habitStatus = false
            )
        }

        val habitData = HabitData(
            date = today,
            habitStatus = statuses,
            habitCompleted = 0
        )

        repository.insertHabitData(habitData)
    }
}
