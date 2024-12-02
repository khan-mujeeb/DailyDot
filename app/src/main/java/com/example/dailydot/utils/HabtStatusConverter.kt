package com.example.dailydot.utils

import androidx.room.TypeConverter
import com.example.dailydot.data.HabitStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitStatusConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromHabitStatusList(list: List<HabitStatus>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toHabitStatusList(json: String?): List<HabitStatus>? {
        val type = object : TypeToken<List<HabitStatus>>() {}.type
        return gson.fromJson(json, type)
    }
}
