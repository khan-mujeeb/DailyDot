package com.example.dailydot.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.utils.HabitStatusConverter
import com.example.dailydot.utils.LocalDateConverter
import java.time.LocalDate

@Database(entities = [Habit::class, HabitData::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter::class, HabitStatusConverter::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitDataDao(): HabitDataDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
