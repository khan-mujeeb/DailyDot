package com.example.dailydot.data

import java.time.LocalDate

object DummyData {
    val habitDataList = listOf(
        HabitData(1, "Exercise", true, LocalDate.of(2024, 11, 1)),
        HabitData(2, "Meditation", false, LocalDate.of(2024, 11, 2)),
        HabitData(3, "Reading", true, LocalDate.of(2024, 11, 3)),
        HabitData(4, "Exercise", true, LocalDate.of(2024, 11, 4)),
        HabitData(5, "Meditation", true, LocalDate.of(2024, 11, 5)),
        // Add more data as needed
    )

}