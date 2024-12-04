package com.example.dailydot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.viewmodel.HabitViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val habits: List<Habit>,
    private val viewModel: HabitViewModel
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    // ViewHolder class to bind the habit item layout
    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        // Inflate the habit_list_item layout
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitTitle.text = habit.habitName

        // Fetch and bind data
        lifecycleOwner.lifecycleScope.launch {

            viewModel.getHabitsByDate(LocalDate.now()).observe(lifecycleOwner) { habitData ->

                // Update checkbox state based on the habit status
                val isCompleted = habitData?.habitStatus?.any { it.uid == habit.uid } == true
                holder.checkBox.isChecked = isCompleted

                // Set listener for checkbox click
                holder.checkBox.setOnClickListener {

                    when {
                        holder.checkBox.isChecked && habitData == null -> {

                            // Insert new habit data when no record exists for today
                            viewModel.insertHabitData(
                                HabitData(
                                    id = 0,
                                    date = LocalDate.now(),
                                    habitStatus = mutableListOf(
                                        HabitStatus(
                                            habit.uid,
                                            habit.habitName,
                                            true
                                        )
                                    ),
                                    habitCompleted = 1
                                )
                            )
                        }

                        holder.checkBox.isChecked -> {
                            // Add to completed list
                            addHabitToCompleted(habit, habitData)
                        }

                        else -> {
                            // Remove from completed list
                            removeHabitFromCompleted(habit, habitData)
                        }
                    }
                }


            }

        }
    }

    private fun addHabitToCompleted(habit: Habit, result: HabitData?) {
        val updatedStatus = result?.habitStatus?.toMutableList() ?: mutableListOf()
        if (updatedStatus.none { it.uid == habit.uid }) {
            updatedStatus.add(HabitStatus(habit.uid, habit.habitName, true))
        }


        viewModel.updateHabitData(
            LocalDate.now(),
            updatedStatus,
            (result?.habitCompleted ?: 0) + 1
        )

    }

    private fun removeHabitFromCompleted(habit: Habit, result: HabitData?) {
        val updatedStatus = result?.habitStatus?.toMutableList() ?: mutableListOf()
        val habitToRemove = updatedStatus.find { it.uid == habit.uid }
        if (habitToRemove != null) {
            updatedStatus.remove(habitToRemove)
        }

        viewModel.updateHabitData(
            LocalDate.now(),
            updatedStatus,
            (result?.habitCompleted ?: 1) - 1
        )

    }

    override fun getItemCount(): Int {
        // Return the total number of habits
        return habits.size
    }
}
