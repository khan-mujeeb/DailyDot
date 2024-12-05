package com.example.dailydot.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.dailydot.R
import com.example.dailydot.data.Habit
import com.example.dailydot.viewmodel.HabitViewModel
import java.util.UUID

object Utils {


    // ******************************************************************************************
    // This function generates a random UID by hashing the text and appending a random UUID
    // ******************************************************************************************
    fun generateRandomUID(text: String): String {
        return "${text.hashCode()}-${UUID.randomUUID()}"
    }

    // ******************************************************************************************
    // this function will return the correct resource file based on the no. of habit completed
    // ******************************************************************************************
    fun getHabitCompletionImageResource(habitCompletionCount: Int, textView: TextView): Int {
        return when (habitCompletionCount) {
            0 -> {
                textView.setTextColor(textView.resources.getColor(R.color.active_text_color))
                R.drawable.heatmap_bg0
            }// No habit
            1 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))
                R.drawable.heatmap_bg1
            } // 1 habit completed
            2 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))
                R.drawable.heatmap_bg2
            } // 2 habit completed
            3 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))

                R.drawable.heatmap_bg3
            } // 3 habit completed
            4 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))

                R.drawable.heatmap_bg4
            }// 4 habit completed
            else -> R.drawable.heatmap_bg4 // Default to max
        }
    }


    // ******************************************************************************************
    //                  This function will show a dialog to add a new habit
    // ******************************************************************************************
    @SuppressLint("MissingInflatedId")
    public fun showAddHabitDialog(context: Context, viewModel: HabitViewModel) {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val btnAddHabit = dialogView.findViewById<Button>(R.id.btnAddHabit)

        // Create and show the dialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnAddHabit.setOnClickListener {
            val habitName = etHabitName.text.toString().trim()

            if (habitName.isNotEmpty()) {

                // Add the habit to the database using ViewModel
                val newHabit = Habit(
                    uid = generateRandomUID(habitName),
                    habitName = habitName
                )

                viewModel.insertHabit(newHabit)

                // Dismiss the dialog
                dialog.dismiss()
            } else {
                etHabitName.error = "Habit name cannot be empty"
            }
        }

        dialog.show()
    }
}