package com.example.dailydot.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "habit_tracking_table")
data class HabitData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: LocalDate,
    val habitStatus: List<HabitStatus>,
    val habitCompleted: Int
)

