package com.example.dailydot.utils


import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.dailydot.R
import com.example.dailydot.data.Habit
import com.example.dailydot.repository.HabitRepository
import com.example.dailydot.viewmodel.HabitViewModel
import com.example.dailydot.viewmodel.HabitViewModelFactory
import java.util.UUID
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddHabitBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: HabitViewModel by activityViewModels {
        HabitViewModelFactory(HabitRepository(requireActivity().application))
    }

    private lateinit var etName: EditText
    private lateinit var btnSave: TextView
    private lateinit var btnClose: ImageView
    private lateinit var tvPreviewName: TextView
    private lateinit var ivPreviewIcon: ImageView
    private lateinit var ivSelectedIcon: ImageView
    private lateinit var viewSelectedColor: View
    private lateinit var colorSwatchContainer: LinearLayout

    private var selectedColorRes: Int = R.color.teal_200
    private var selectedIconRes: Int = R.drawable.outline_add_box_24

    private val colorOptions = listOf(
        R.color.teal_200,
        R.color.blue_800,
        R.color.green_700,
        R.color.orange_800,
        R.color.pink_700,
        R.color.cyan_700,
        R.color.example_1_selection_color,
        R.color.brown_700,
        R.color.blue_grey_700,
        R.color.example_4_grey_past
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_add_habit_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.etHabitName)
        btnSave = view.findViewById(R.id.btnSave)
        btnClose = view.findViewById(R.id.btnClose)
        tvPreviewName = view.findViewById(R.id.tvPreviewName)
        ivPreviewIcon = view.findViewById(R.id.ivPreviewIcon)
        ivSelectedIcon = view.findViewById(R.id.ivSelectedIcon)
        viewSelectedColor = view.findViewById(R.id.viewSelectedColor)
        colorSwatchContainer = view.findViewById(R.id.colorSwatchContainer)

        setupColorSwatches()
        updatePreview()

        btnClose.setOnClickListener { dismiss() }

        etName.addTextChangedListener {
            val text = it?.toString().orEmpty()
            tvPreviewName.text = if (text.isNotBlank()) text else getString(R.string.habit_name_hint)
            setSaveEnabled(text.isNotBlank())
        }

        view.findViewById<View>(R.id.rowIcon).setOnClickListener {
            showIconPicker()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveHabit(name)
            }
        }
    }

    private fun setSaveEnabled(enabled: Boolean) {
        btnSave.isEnabled = enabled
        btnSave.alpha = if (enabled) 1f else 0.4f
    }

    private fun updateColor(colorRes: Int) {
        selectedColorRes = colorRes
        val colorInt = ContextCompat.getColor(requireContext(), colorRes)

        (viewSelectedColor.background?.mutate() as? GradientDrawable)?.let {
            it.setColor(colorInt)
            viewSelectedColor.background = it
        }
        updatePreview()
        highlightSelectedSwatch()
    }

    private fun updatePreview() {
        HabitIconStyler.apply(ivPreviewIcon, selectedIconRes, selectedColorRes)
        HabitIconStyler.apply(ivSelectedIcon, selectedIconRes, selectedColorRes)
    }

    private fun setupColorSwatches() {
        colorSwatchContainer.removeAllViews()

        colorOptions.forEach { colorRes ->
            val swatch = View(requireContext()).apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_color_swatch)
                    ?.mutate()
                layoutParams = LinearLayout.LayoutParams(42.dp(), 42.dp()).apply {
                    marginEnd = 10.dp()
                }
                setOnClickListener {
                    updateColor(colorRes)
                }
            }

            (swatch.background as? GradientDrawable)?.setColor(
                ContextCompat.getColor(requireContext(), colorRes)
            )
            swatch.tag = colorRes
            colorSwatchContainer.addView(swatch)
        }

        highlightSelectedSwatch()
    }

    private fun highlightSelectedSwatch() {
        for (index in 0 until colorSwatchContainer.childCount) {
            val swatch = colorSwatchContainer.getChildAt(index)
            val drawable = swatch.background?.mutate() as? GradientDrawable ?: continue
            val isSelected = swatch.tag == selectedColorRes

            drawable.setStroke(
                if (isSelected) 3.dp() else 1.dp(),
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.active_text_color else R.color.card_stroke
                )
            )
            swatch.background = drawable
        }
    }

    private fun showIconPicker() {
        HabitIconPickerBottomSheet { template ->
            // apply chosen template
            selectedIconRes = template.iconRes
            updateColor(template.colorRes)

            if (etName.text.isNullOrBlank()) {
                etName.setText(template.name)
                etName.setSelection(template.name.length)
            }
        }.show(parentFragmentManager, "IconPicker")
    }

    private fun saveHabit(habitName: String) {
        val newHabit = Habit(
            uid = generateRandomUID(habitName),
            habitName = habitName,
            habitIcon = selectedIconRes,
            habitColor = selectedColorRes
        )

        viewModel.addHabitAndEnsureTodayData(newHabit)
        dismiss()
    }

    private fun generateRandomUID(text: String): String {
        return "${text.hashCode()}-${UUID.randomUUID()}"
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()


}
