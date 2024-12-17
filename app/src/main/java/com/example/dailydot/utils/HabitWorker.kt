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
        val habitDao = HabitDatabase.getDatabase(applicationContext).habitDao()
        val habitDataDao = HabitDatabase.getDatabase(applicationContext).habitDataDao()


        val currentDate = LocalDate.now()
        val habits = habitDao.getAllHabitsList()
        val habitStatusList = mutableListOf<HabitStatus>()




        // Create a list of habit status for the current date and mark all habits as incomplete
        habits.forEach { habit ->
            habitStatusList.add(
                HabitStatus(
                    uid = habit.uid, habitName = habit.habitName, habitStatus = false
                )
            )
        }

        // Insert the habit data for the current date
        habitDataDao.insertHabitData(
            HabitData(
                id = 0,
                date = currentDate,
                habitStatus = habitStatusList,
                habitCompleted = 0
            )
        )




        return Result.success()
    }
}
