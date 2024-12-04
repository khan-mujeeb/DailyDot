package com.example.dailydot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.HabitStatus

class PastHabitAdapter(private val habitCompleted: List<HabitStatus>) :
    RecyclerView.Adapter<PastHabitAdapter.PastHabitViewHolder>() {

    // ViewHolder class for individual list items
    class PastHabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitStatusIcon: ImageView = view.findViewById(R.id.habitStatus)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastHabitViewHolder {
        // Inflate the layout for each item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.previous_habit_list_item, parent, false)
        return PastHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastHabitViewHolder, position: Int) {
        val habitStatus = habitCompleted[position]

        // Set the habit title
        holder.habitTitle.text = habitStatus.habitName

        // Update the status icon based on whether the habit was completed
        if (habitStatus.habitStatus) {
            holder.habitStatusIcon.setImageResource(R.drawable.baseline_check_box_24) // Completed icon
        } else {
            holder.habitStatusIcon.setImageResource(R.drawable.baseline_disabled_by_default_24) // Not completed icon
        }
    }

    override fun getItemCount(): Int {
        // Return the total number of items in the list
        return habitCompleted.size
    }
}
