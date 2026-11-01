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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.data.HabitStatus
import com.example.dailydot.utils.HabitIconStyler
import com.example.dailydot.viewmodel.HabitViewModel

class HabitAdapter(
    private val context: Context,
    private var habitData: HabitData,
    private var habits: List<Habit>,  // master habit list
    private val viewModel: HabitViewModel,
    private val onHabitLongPress: (Habit, HabitData, Float, Float) -> Unit, // (habit, dayData, x, y)
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    init {
        setHasStableIds(true)
    }

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.habitCheckBox)
        val habitTitle: TextView = view.findViewById(R.id.habitTitle)
        val habitProgress: TextView = view.findViewById(R.id.tvHabitProgress)
        val habitNote: TextView = view.findViewById(R.id.tvHabitNote)
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
        val target = status.goalTarget.coerceAtLeast(1)
        val displayedProgress = if (status.habitStatus) target else status.progress.coerceIn(0, target)
        val goalUnit = status.goalUnit.orEmpty().ifBlank { "done" }
        val note = status.note.orEmpty()
        holder.habitProgress.text = "$displayedProgress/$target $goalUnit"
        holder.habitNote.isVisible = note.isNotBlank()
        holder.habitNote.text = note
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

            if (holder.checkBox.isChecked) {
                holder.checkBox.setOnCheckedChangeListener(null)
                holder.checkBox.isChecked = false
                viewModel.removeHabitFromCompleted(status, habitData)
            } else {
                viewModel.advanceHabitProgress(status, habitData)
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

    override fun getItemId(position: Int): Long =
        habitData.habitStatus[position].uid.hashCode().toLong()

    fun updateData(habitData: HabitData, habits: List<Habit>) {
        this.habitData = habitData
        this.habits = habits
        notifyDataSetChanged()
    }

    fun currentHabitData(): HabitData = habitData
}
