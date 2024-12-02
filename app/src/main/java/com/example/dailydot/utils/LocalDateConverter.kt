package com.example.dailydot.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {
    @TypeConverter
    fun fromString(value: String): LocalDate {
        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_DATE)
    }
}

