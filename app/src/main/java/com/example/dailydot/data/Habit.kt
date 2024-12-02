package com.example.dailydot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey()
    val uid: String,
    val habitName: String
)
