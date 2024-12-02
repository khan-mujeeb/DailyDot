package com.example.dailydot.data

import androidx.room.Entity

@Entity(tableName = "habit_table")
data class Habit(
    val uid: Int,
    val habitName: String
)
