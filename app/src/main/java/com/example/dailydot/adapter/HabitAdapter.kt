package com.example.dailydot.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.ActionType
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.utils.Utils.threeDTouchClickListener
import com.example.dailydot.viewmodel.HabitViewModel
import java.time.LocalDate


class HabitAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val habits: List<Habit>,
    private val habitData: HabitData,
    private val viewModel: HabitViewModel,
    private val onHabitAction: (Habit, ActionType, Float, Float) -> Unit,
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitTitle.text = habit.habitName

        // long press listener to delete the habit item
        threeDTouchClickListener(holder, habit, onHabitAction)


        // Update checkbox state based on the habit status
        val isCompleted =
            habitData?.habitStatus?.any { it.uid == habit.uid && it.habitStatus } == true
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
                    viewModel.addHabitToCompleted(habit, habitData)
                }

                else -> {
                    // Remove from completed list
                    viewModel.removeHabitFromCompleted(habit, habitData)
                }

            }


        }


    }


    override fun getItemCount(): Int {
        // Return the total number of habits
        return habits.size
    }

}
