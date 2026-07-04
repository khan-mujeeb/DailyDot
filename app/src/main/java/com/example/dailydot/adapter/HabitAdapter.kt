package com.example.dailydot.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.utils.HabitIconStyler
import com.example.dailydot.viewmodel.HabitViewModel

class HabitAdapter(
    private val context: Context,
    private val habitData: HabitData,
    private val habits: List<Habit>,  // master habit list
    private val viewModel: HabitViewModel,
    private val onHabitLongPress: (Habit, HabitData, Float, Float) -> Unit, // (habit, dayData, x, y)
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
        val icon: ImageView = view.findViewById(R.id.ivIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_list_item, parent, false)
        return HabitViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val status: HabitStatus = habitData.habitStatus[position]

        // Find corresponding Habit entity by uid
        val habitEntity: Habit? = habits.firstOrNull { it.uid == status.uid }

        val iconRes = habitEntity?.habitIcon ?: status.habitIcon.takeIf { it != 0 }
        val colorRes = habitEntity?.habitColor ?: status.habitColor.takeIf { it != 0 }

        holder.habitTitle.text = habitEntity?.habitName ?: status.habitName
        HabitIconStyler.apply(
            holder.icon,
            iconRes ?: R.drawable.outline_add_box_24,
            colorRes ?: R.color.teal_200
        )

        // --- Checkbox setup ---
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = status.habitStatus

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.addHabitToCompleted(status, habitData)
            } else {
                viewModel.removeHabitFromCompleted(status, habitData)
            }
        }

        // --- Click row = toggle checkbox + animate ---
        holder.itemView.setOnClickListener { view ->
            val newState = !holder.checkBox.isChecked

            // small click animation
            view.animate()
                .scaleX(0.97f)
                .scaleY(0.97f)
                .setDuration(80)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .start()
                }
                .start()

            // avoid double-calls from checkbox listener
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.isChecked = newState

            if (newState) {
                viewModel.addHabitToCompleted(status, habitData)
            } else {
                viewModel.removeHabitFromCompleted(status, habitData)
            }

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.addHabitToCompleted(status, habitData)
                } else {
                    viewModel.removeHabitFromCompleted(status, habitData)
                }
            }
        }

        // --- Long press = edit/delete popup ---
        var touchX = 0f
        var touchY = 0f

        holder.itemView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                touchX = event.rawX
                touchY = event.rawY
            }
            false
        }

        holder.itemView.setOnLongClickListener {
            habitEntity?.let { habit ->
                onHabitLongPress(habit, habitData, touchX, touchY)
            }
            true
        }
    }

    override fun getItemCount(): Int = habitData.habitStatus.size
}
