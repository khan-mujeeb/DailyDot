package com.example.dailydot.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dailydot.data.Habit

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("SELECT * FROM habit_table")
    fun getAllHabits(): LiveData<List<Habit>>

    @Delete
    suspend fun deleteHabit(habit: Habit)
}