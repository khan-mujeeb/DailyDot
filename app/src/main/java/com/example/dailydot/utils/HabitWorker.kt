package com.example.dailydot.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.database.HabitDatabase
import java.time.LocalDate

class HabitWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = HabitDatabase.getDatabase(applicationContext)
        val habitDao = db.habitDao()
        val habitDataDao = db.habitDataDao()

        val currentDate = LocalDate.now()

        // Avoid inserting duplicate record for same date
        val existing = habitDataDao.getOnceHabitsByDate(currentDate)
        if (existing != null) return Result.success()

        val habits = habitDao.getAllHabitsList()
        val habitStatusList = habits.map { habit ->
            HabitStatus(
                uid = habit.uid,
                habitName = habit.habitName,
                habitStatus = false
            )
        }

        val habitData = HabitData(
            date = currentDate,
            habitStatus = habitStatusList,
            habitCompleted = 0
        )

        habitDataDao.insertHabitData(habitData)

        return Result.success()
    }
}
