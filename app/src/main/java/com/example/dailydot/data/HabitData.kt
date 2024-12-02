package com.example.dailydot.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "habit_tracking_table")
data class HabitData(
    val date: LocalDate,
    val id: Int,
    val habitStatus: List<HabitStatus>
)

