package com.example.dailydot.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.utils.HabitIconStyler

class PastHabitAdapter(
    private val context: Context,
    private val habitStatus: List<HabitStatus>
) : RecyclerView.Adapter<PastHabitAdapter.PastHabitViewHolder>() {

    class PastHabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
        val cvHabitStatus: View = view.findViewById(R.id.cvHabitCompletionStatus)
        val tvHabitStatus: TextView = view.findViewById(R.id.tvHabitCompletionStatus)
        val ivIcon: ImageView = view.findViewById(R.id.ivPreviewIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastHabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.previous_habit_list_item, parent, false)
        return PastHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastHabitViewHolder, position: Int) {
        val status = habitStatus[position]

        holder.habitTitle.text = status.habitName
        HabitIconStyler.apply(
            holder.ivIcon,
            status.habitIcon,
            status.habitColor
        )

        // set status color and text
        when (status.habitStatus) {
            true -> {
                holder.cvHabitStatus.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.green_700)
                )
                holder.tvHabitStatus.text = "Completed"
            }
            false -> {
                holder.cvHabitStatus.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.red_700)
                )
                holder.tvHabitStatus.text = "Not completed"
            }
        }
    }

    override fun getItemCount(): Int = habitStatus.size
}
