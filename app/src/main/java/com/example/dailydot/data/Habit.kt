package com.example.dailydot.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey
    val uid: String,          // unique ID
    val habitName: String,   // title shown in UI
    val habitIcon: Int,   // example → R.drawable.ic_menu_book
    val habitColor: Int   // example → R.color.teal_200

)
