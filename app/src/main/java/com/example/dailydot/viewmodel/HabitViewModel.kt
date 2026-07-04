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

    // ---------------- HABITS (MASTER LIST) ----------------

    fun insertHabit(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
    }

    fun addHabitAndEnsureTodayData(habit: Habit) = viewModelScope.launch {
        repository.insertHabit(habit)
        ensureTodayHabitDataIncludes(habit)
    }

    fun getAllHabits(): LiveData<List<Habit>> = repository.getAllHabits()

    fun deleteHabit(habit: Habit) = viewModelScope.launch {
        repository.deleteHabit(habit)
    }

    fun updateHabit(habit: Habit) = viewModelScope.launch {
        repository.updateHabit(habit)
    }

    // Return full list once (not LiveData)
    suspend fun getAllHabitsList(): List<Habit> = repository.getAllHabitsList()

    // ---------------- HABIT TRACKING (PER DAY) ----------------

    fun insertHabitData(habitData: HabitData) = viewModelScope.launch {
        repository.insertHabitData(habitData)
    }

    fun updateHabitData(
        date: LocalDate,
        habitStatus: List<HabitStatus>,
        habitCompleted: Int
    ) {
        viewModelScope.launch {
            repository.updateHabitData(date, habitStatus, habitCompleted)
        }
    }

    fun getHabitDataByDate(date: LocalDate): LiveData<HabitData?> =
        repository.getHabitDataByDate(date)

    fun getAllHabitTrackingData(): LiveData<List<HabitData>> =
        repository.getAllHabitTrackingData()

    fun getHabitTrackingDataBetween(
        startDate: LocalDate,
        endDate: LocalDate
    ): LiveData<List<HabitData>> =
        repository.getHabitTrackingDataBetween(startDate, endDate)

    suspend fun getOnceHabitsByDate(date: LocalDate): HabitData? =
        repository.getOnceHabitsByDate(date)

    // ---------------- CHECKBOX HELPERS ----------------

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

    // ---------------- CREATE TODAY'S HABITDATA IF NEEDED ----------------

    suspend fun createTodayHabitDataIfMissing() {
        val today = LocalDate.now()
        val existing: HabitData? = repository.getOnceHabitsByDate(today)
        if (existing != null) return

        val allHabits = repository.getAllHabitsList()
        if (allHabits.isEmpty()) return

        val statuses = allHabits.map { habit ->
            HabitStatus(
                uid = habit.uid,
                habitName = habit.habitName,
                habitStatus = false,
                habitIcon = habit.habitIcon,
                habitColor = habit.habitColor
            )
        }

        val habitData = HabitData(
            date = today,
            habitStatus = statuses,
            habitCompleted = 0
        )

        repository.insertHabitData(habitData)
    }

    // ---------------- ENSURE TODAY HAS NEW HABIT ----------------

    suspend fun ensureTodayHabitDataIncludes(newHabit: Habit) {
        val today = LocalDate.now()
        val existing: HabitData? = repository.getOnceHabitsByDate(today)

        if (existing == null) {
            // No row, create from all habits
            val allHabits = repository.getAllHabitsList()
            if (allHabits.isEmpty()) return

            val statuses = allHabits.map { habit ->
                HabitStatus(
                    uid = habit.uid,
                    habitName = habit.habitName,
                    habitStatus = false,
                    habitIcon = habit.habitIcon,
                    habitColor = habit.habitColor
                )
            }

            val habitData = HabitData(
                date = today,
                habitStatus = statuses,
                habitCompleted = 0
            )
            repository.insertHabitData(habitData)
        } else {
            // Row exists: add this habit if missing
            val updatedStatuses = existing.habitStatus.toMutableList()
            val alreadyPresent = updatedStatuses.any { it.uid == newHabit.uid }

            if (!alreadyPresent) {
                updatedStatuses.add(
                    HabitStatus(
                        uid = newHabit.uid,
                        habitName = newHabit.habitName,
                        habitStatus = false,
                        habitIcon = newHabit.habitIcon,
                        habitColor = newHabit.habitColor
                    )
                )

                repository.updateHabitData(
                    date = existing.date,
                    habitStatus = updatedStatuses,
                    habitCompleted = existing.habitCompleted
                )
            }
        }
    }

    // ---------------- NEW: EDIT + DELETE WITH TRACKING UPDATE ----------------

    fun editHabitNameForDay(habit: Habit, newName: String, habitData: HabitData) {
        viewModelScope.launch {
            // 1) update Habit in master table
            val updatedHabit = habit.copy(habitName = newName)
            repository.updateHabit(updatedHabit)

            // 2) update this day's HabitData list
            val updatedStatuses = habitData.habitStatus.map {
                if (it.uid == habit.uid) it.copy(habitName = newName) else it
            }

            val newCompleted = updatedStatuses.count { it.habitStatus }

            repository.updateHabitData(
                date = habitData.date,
                habitStatus = updatedStatuses,
                habitCompleted = newCompleted
            )
        }
    }

    fun deleteHabitAndUpdateTracking(habit: Habit, habitData: HabitData) {
        viewModelScope.launch {
            // 1) remove from master table
            repository.deleteHabit(habit)

            // 2) remove from this day's HabitData.status list
            val updatedStatuses = habitData.habitStatus.filter { it.uid != habit.uid }
            val newCompleted = updatedStatuses.count { it.habitStatus }

            repository.updateHabitData(
                date = habitData.date,
                habitStatus = updatedStatuses,
                habitCompleted = newCompleted
            )
        }
    }
}
