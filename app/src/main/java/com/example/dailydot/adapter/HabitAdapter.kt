package com.example.dailydot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.viewmodel.HabitViewModel

class HabitAdapter(
    private val habitData: HabitData,
    private val viewModel: HabitViewModel
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_list_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habitStatus: HabitStatus = habitData.habitStatus[position]

        holder.habitTitle.text = habitStatus.habitName
        holder.checkBox.isChecked = habitStatus.habitStatus

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.addHabitToCompleted(habitStatus, habitData)
            } else {
                viewModel.removeHabitFromCompleted(habitStatus, habitData)
            }
        }
    }

    override fun getItemCount(): Int = habitData.habitStatus.size
}
