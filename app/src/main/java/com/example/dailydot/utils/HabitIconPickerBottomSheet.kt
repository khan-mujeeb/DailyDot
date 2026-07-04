package com.example.dailydot.utils


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.dailydot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.adapter.HabitIconAdapter
import com.example.dailydot.data.HabitTemplate

class HabitIconPickerBottomSheet(
    private val onIconSelected: (HabitTemplate) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habit_icon_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvIcons)
        val search = view.findViewById<EditText>(R.id.etSearchIcons)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val templates = listOf(
            HabitTemplate("Read", R.drawable.outline_menu_book_24, R.color.teal_200),
            HabitTemplate("Journal", R.drawable.outline_stylus_note_24, R.color.example_1_selection_color),
            HabitTemplate("Practice music", R.drawable.outline_music_note_24, R.color.pink_700),
            HabitTemplate("Code", R.drawable.outline_keyboard_24, R.color.blue_800),
            HabitTemplate("Commute", R.drawable.outline_motorcycle_24, R.color.brown_700),
            HabitTemplate("Photo walk", R.drawable.outline_linked_camera_24, R.color.blue_grey_700),
            HabitTemplate("Budget", R.drawable.outline_calculate_24, R.color.cyan_700),
            HabitTemplate("Workout", R.drawable.outline_exercise_24, R.color.orange_800),
            HabitTemplate("Run", R.drawable.outline_directions_run_24, R.color.green_700),
            HabitTemplate("Cook", R.drawable.outline_cookie_24, R.color.red_800),
            HabitTemplate("Build", R.drawable.baseline_add_24, R.color.teal_700),
            HabitTemplate("Review", R.drawable.baseline_check_box_24, R.color.example_4_green),
            HabitTemplate("Custom", R.drawable.outline_add_box_24, R.color.example_4_grey_past)
        )

        val adapter = HabitIconAdapter(templates) { template ->
            onIconSelected(template)
            dismiss()
        }
        rv.adapter = adapter

        search.addTextChangedListener {
            adapter.filter(it?.toString().orEmpty())
        }
    }
}
