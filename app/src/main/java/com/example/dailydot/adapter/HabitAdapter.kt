package com.example.dailydot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.Habit

class HabitAdapter(private val habits: List<Habit>) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    // ViewHolder class to bind the habit item layout
    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        // Inflate the habit_list_item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        // Bind the data for the current habit
        val habit = habits[position]
        holder.habitTitle.text = habit.habitName

        // Handle checkbox state change
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            // You can perform actions when the checkbox is checked/unchecked
            // For example, updating the status of the habit in your data model
            if (isChecked) {
                // Habit marked as completed
            } else {
                // Habit marked as not completed
            }
        }
    }

    override fun getItemCount(): Int {
        // Return the total number of habits
        return habits.size
    }
}
