package com.example.dailydot.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.utils.HabitStatusConverter
import com.example.dailydot.utils.LocalDateConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Habit::class, HabitData::class], version = 4, exportSchema = false)

@TypeConverters(LocalDateConverter::class, HabitStatusConverter::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitDataDao(): HabitDataDao

    companion object {
        private const val DB_NAME = "habit_database"

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_3_4)
                    // DEV ONLY: rebuild DB when schema changes
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // optional seeding
                            CoroutineScope(Dispatchers.IO).launch {
                                // val database = getDatabase(context.applicationContext)
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE habit_table ADD COLUMN goalTarget INTEGER NOT NULL DEFAULT 1"
                )
                db.execSQL(
                    "ALTER TABLE habit_table ADD COLUMN goalUnit TEXT NOT NULL DEFAULT 'done'"
                )
            }
        }
    }
}
