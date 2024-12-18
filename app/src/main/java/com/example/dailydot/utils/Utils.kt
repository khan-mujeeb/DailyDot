package com.example.dailydot.utils

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.dailydot.R
import com.example.dailydot.adapter.HabitAdapter
import com.example.dailydot.data.ActionType
import com.example.dailydot.data.Habit
import com.example.dailydot.databinding.ActivityMainBinding
import com.example.dailydot.viewmodel.HabitViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
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
                textView.setTextColor(textView.resources.getColor(R.color.active_text_color))
                R.drawable.heatmap_bg0
            }// No habit
            1 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))
                R.drawable.heatmap_bg1
            } // 1 habit completed
            2 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))
                R.drawable.heatmap_bg2
            } // 2 habit completed
            3 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))

                R.drawable.heatmap_bg3
            } // 3 habit completed
            4 -> {
                textView.setTextColor(textView.resources.getColor(R.color.date_text_resource))

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
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_habit, null)
        val etHabitName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val btnAddHabit = dialogView.findViewById<Button>(R.id.btnAddHabit)

        // Create and show the dialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnAddHabit.setOnClickListener {
            val habitName = etHabitName.text.toString().trim()

            if (habitName.isNotEmpty()) {

                // Add the habit to the database using ViewModel
                val newHabit = Habit(
                    uid = generateRandomUID(habitName),
                    habitName = habitName
                )

                viewModel.insertHabit(newHabit)

                lifecycleOwner.lifecycleScope.launch {


                    val habits = viewModel.getOnceHabitsByDate(LocalDate.now())
                    viewModel.addNewHabitSetupData(newHabit, habits)

                }

                // Dismiss the dialog
                dialog.dismiss()
            } else {
                etHabitName.error = "Habit name cannot be empty"
            }
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
        x: Float,
        y: Float
    ) {
        // Inflate the dialog layout
        val popupView = LayoutInflater.from(context).inflate(R.layout.threed_touch_popup, null)
        val btnEditHabit = popupView.findViewById<TextView>(R.id.editTextBtn)
        val btnDeleteHabit = popupView.findViewById<TextView>(R.id.deleteTextBtn)
        val tvHabitName = popupView.findViewById<TextView>(R.id.tvHabitName)

        tvHabitName.text = habit.habitName

        // Create PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Handle button clicks
        btnEditHabit.setOnClickListener {

            viewModel.updateHabit(habit)
            Toast.makeText(context, "Habit updated", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        btnDeleteHabit.setOnClickListener {
            viewModel.deleteHabit(habit)
            Toast.makeText(context, "Habit deleted", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        // Show the PopupWindow at the specified position
        popupWindow.showAtLocation(binding.root, Gravity.NO_GRAVITY, x.toInt(), y.toInt())
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