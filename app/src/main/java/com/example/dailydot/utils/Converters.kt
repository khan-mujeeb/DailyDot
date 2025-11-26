package com.example.dailydot.utils

import androidx.room.TypeConverter
import com.example.dailydot.data.HabitStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Converters {
    private val gson = Gson()
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? = date?.format(dateFormatter)

    @TypeConverter
    @JvmStatic
    fun toLocalDate(dateString: String?): LocalDate? =
        dateString?.let { LocalDate.parse(it, dateFormatter) }

    @TypeConverter
    @JvmStatic
    fun fromHabitStatusList(list: List<HabitStatus>?): String? =
        if (list == null) null else gson.toJson(list)

    @TypeConverter
    @JvmStatic
    fun toHabitStatusList(json: String?): List<HabitStatus>? =
        if (json.isNullOrEmpty()) null
        else gson.fromJson(json, object : TypeToken<List<HabitStatus>>() {}.type)
}
