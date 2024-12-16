package com.example.dailydot.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "habit_tracking_table")
data class HabitData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate = LocalDate.now(),
    val habitStatus: List<HabitStatus> = emptyList(),
    val habitCompleted: Int = 0
)

