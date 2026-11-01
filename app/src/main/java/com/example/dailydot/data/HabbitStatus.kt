package com.example.dailydot.data

import com.example.dailydot.R

data class HabitStatus(
    val uid: String,
    val habitName: String,
    val habitStatus: Boolean,   // true = completed
    val habitIcon: Int = R.drawable.outline_add_box_24,
    val habitColor: Int = R.color.teal_200,
    val progress: Int = if (habitStatus) 1 else 0,
    val goalTarget: Int = 1,
    val goalUnit: String? = "done",
    val note: String? = ""
)
