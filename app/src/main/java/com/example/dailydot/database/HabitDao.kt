package com.example.dailydot.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailydot.data.Habit

@Dao
interface HabitDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("UPDATE habit_table SET habitName = :name, habitIcon = :icon, habitColor = :color, goalTarget = :goalTarget, goalUnit = :goalUnit WHERE uid = :id")
    suspend fun updateHabit(id: String, name: String, icon: Int, color: Int, goalTarget: Int, goalUnit: String)



    @Query("SELECT * FROM habit_table")
    fun getAllHabits(): LiveData<List<Habit>>

    @Query("SELECT * FROM habit_table")
    suspend fun getAllHabitsList(): List<Habit>

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("UPDATE habit_table SET habitName = :habitName WHERE uid = :habitId")
    suspend fun updateHabitName(habitId: String, habitName: String)
}
