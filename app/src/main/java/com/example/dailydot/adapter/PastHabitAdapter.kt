package com.example.dailydot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.HabitStatus

class PastHabitAdapter(
    private val habitStatuses: List<HabitStatus>
) : RecyclerView.Adapter<PastHabitAdapter.PastHabitViewHolder>() {

    class PastHabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitStatusIcon: ImageView = view.findViewById(R.id.habitStatus)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastHabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.previous_habit_list_item, parent, false)
        return PastHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastHabitViewHolder, position: Int) {
        val status = habitStatuses[position]
        holder.habitTitle.text = status.habitName
        holder.habitStatusIcon.setImageResource(
            if (status.habitStatus) R.drawable.baseline_check_box_24
            else R.drawable.baseline_disabled_by_default_24
        )
    }

    override fun getItemCount(): Int = habitStatuses.size
}
