package com.example.dailydot.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import java.time.LocalDate

@Dao
interface HabitDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitData(habitData: HabitData)

    @Query("UPDATE habit_tracking_table SET habitStatus = :habitStatus, habitCompleted = :habitCompleted WHERE date = :date")
    suspend fun updateHabitData(date: LocalDate, habitStatus: List<HabitStatus>, habitCompleted: Int)



    @Query("SELECT * FROM habit_tracking_table WHERE date = :date")
    fun getHabitsByDate(date: LocalDate): LiveData<HabitData>

    @Query("SELECT * FROM habit_tracking_table")
    fun getAllHabits(): LiveData<List<HabitData>>
}
