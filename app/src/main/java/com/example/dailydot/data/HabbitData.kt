package com.example.dailydot.data

import java.time.LocalDate

data class HabitData(
    val id: Int,
    val habitName: String,
    val status: Boolean,
    val date: LocalDate
)

