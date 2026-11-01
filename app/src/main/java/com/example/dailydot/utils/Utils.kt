package com.example.dailydot.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.example.dailydot.R
import com.example.dailydot.adapter.HabitAdapter
import com.example.dailydot.data.ActionType
import com.example.dailydot.data.Habit
import com.example.dailydot.data.HabitData
import com.example.dailydot.databinding.ActivityMainBinding
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
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.active_text_color))
                R.drawable.heatmap_bg0
            }// No habit
            1 -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.date_text_resource))
                R.drawable.heatmap_bg1
            } // 1 habit completed
            2 -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.date_text_resource))
                R.drawable.heatmap_bg2
            } // 2 habit completed
            3 -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.date_text_resource))

                R.drawable.heatmap_bg3
            } // 3 habit completed
            4 -> {
                textView.setTextColor(ContextCompat.getColor(textView.context, R.color.date_text_resource))

                R.drawable.heatmap_bg4
            }// 4 habit completed
            else -> R.drawable.heatmap_bg4 // Default to max
        }
    }

    // ******************************************************************************************
    //                              loading alert dialog / loader
    // ******************************************************************************************
    fun showLoaderDialog(context: Context): AlertDialog {

        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loader, null)

        // Find the ImageView in the inflated view
        val loaderImageView = dialogView.findViewById<ImageView>(R.id.ivLoader)
        Glide.with(context).asGif().load(R.drawable.fidget).into(loaderImageView)

        // Create AlertDialog
        val loaderDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false) // Prevent dismissal when tapping outside
            .create()

        // Show the dialog


        return loaderDialog
    }


    // ******************************************************************************************
    //                  This function will show a dialog to add a new habit
    // ******************************************************************************************
    @SuppressLint("MissingInflatedId")
    fun showAddHabitDialog(
        context: Context,
        viewModel: HabitViewModel,
        lifecycleOwner: LifecycleOwner
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val btnAddHabit = dialogView.findViewById<Button>(R.id.btnAddHabit)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnAddHabit.setOnClickListener {
            val habitName = etHabitName.text.toString().trim()

            if (habitName.isEmpty()) {
                etHabitName.error = "Habit name cannot be empty"
                return@setOnClickListener
            }

            val newHabit = com.example.dailydot.data.Habit(
                uid = generateRandomUID(habitName),
                habitName = habitName,
                habitIcon = R.drawable.outline_add_box_24,
                habitColor = R.color.teal_200,
                goalTarget = 1,
                goalUnit = "done"
            )

            viewModel.addHabitAndEnsureTodayData(newHabit)

            Toast.makeText(context, "Habit added", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }




    // ******************************************************************************************
    //                  This function will show a dialog to edit and delete  a habit
    // ******************************************************************************************
    @SuppressLint("InflateParams", "MissingInflatedId")
    fun showEditDeleteHabitPopup(
        binding: ActivityMainBinding,
        context: Context,
        viewModel: HabitViewModel,
        habit: Habit,
        habitData: HabitData,
        x: Float,
        y: Float
    ) {
        // Inflate the dialog layout
        val popupView = LayoutInflater.from(context).inflate(R.layout.threed_touch_popup, null)
        val btnEditHabit = popupView.findViewById<TextView>(R.id.editTextBtn)
        val btnNoteHabit = popupView.findViewById<TextView>(R.id.noteTextBtn)
        val btnDeleteHabit = popupView.findViewById<TextView>(R.id.deleteTextBtn)
        val tvHabitName = popupView.findViewById<TextView>(R.id.tvHabitName)

        tvHabitName.text = habit.habitName

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // --------- EDIT HABIT NAME ----------
        btnEditHabit.setOnClickListener {
            popupWindow.dismiss()

            showHabitInputDialog(
                context = context,
                title = context.getString(R.string.edit_habit_title),
                description = context.getString(R.string.edit_habit_description),
                initialText = habit.habitName,
                hint = context.getString(R.string.habit_name_hint),
                multiline = false
            ) { newName ->
                if (newName.isNotEmpty()) {
                    viewModel.editHabitNameForDay(habit, newName, habitData)
                    Toast.makeText(context, "Habit updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnNoteHabit.setOnClickListener {
            popupWindow.dismiss()

            val status = habitData.habitStatus.firstOrNull { it.uid == habit.uid }
            showHabitInputDialog(
                context = context,
                title = context.getString(R.string.habit_note_title),
                description = context.getString(R.string.habit_note_description),
                initialText = status?.note.orEmpty(),
                hint = context.getString(R.string.habit_note_hint),
                multiline = true
            ) { note ->
                status?.let {
                    viewModel.updateHabitNote(it, habitData, note)
                }
                Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
            }
        }

        // --------- DELETE HABIT ----------
        btnDeleteHabit.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete habit")
                .setMessage("Are you sure you want to delete this habit?")
                .setPositiveButton("Delete") { dialog, _ ->
                    // This removes from Habit table + today's HabitData
                    viewModel.deleteHabitAndUpdateTracking(habit, habitData)
                    Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    popupWindow.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // Show the popup at touch position
        popupWindow.showAtLocation(binding.root, Gravity.NO_GRAVITY, x.toInt(), y.toInt())
    }

    private fun showHabitInputDialog(
        context: Context,
        title: String,
        description: String,
        initialText: String,
        hint: String,
        multiline: Boolean,
        onSave: (String) -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_habit_input, null)
        val titleView = dialogView.findViewById<TextView>(R.id.tvInputTitle)
        val descriptionView = dialogView.findViewById<TextView>(R.id.tvInputDescription)
        val input = dialogView.findViewById<EditText>(R.id.etHabitInput)
        val cancel = dialogView.findViewById<TextView>(R.id.btnInputCancel)
        val save = dialogView.findViewById<TextView>(R.id.btnInputSave)

        titleView.text = title
        descriptionView.text = description
        input.apply {
            setText(initialText)
            setSelection(text.length)
            this.hint = hint
            minLines = if (multiline) 3 else 1
            maxLines = if (multiline) 5 else 1
            isSingleLine = !multiline
        }

        val dialog = AlertDialog.Builder(context).setView(dialogView).create()
        cancel.setOnClickListener { dialog.dismiss() }
        save.setOnClickListener {
            onSave(input.text.toString().trim())
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }



    // ******************************************************************************************
    //                  This function will show a dialog to edit and delete  a habit
    // ******************************************************************************************
    @SuppressLint("ClickableViewAccessibility")
    fun threeDTouchClickListener(
        holder: HabitAdapter.HabitViewHolder,
        habit: Habit,
        onHabitAction: (Habit, ActionType, Float, Float) -> Unit
    ) {

        var touchX = 0f
        var touchY = 0f

        holder.itemView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Store touch coordinates
                touchX = event.rawX
                touchY = event.rawY
            }
            false // Return false to allow other listeners like OnLongClickListener to work
        }


        holder.itemView.setOnLongClickListener {
            // Trigger haptic feedback on hover
            onHabitAction(
                habit,
                ActionType.EDIT,
                touchX,
                touchY
            )


            true
        }
    }

}
