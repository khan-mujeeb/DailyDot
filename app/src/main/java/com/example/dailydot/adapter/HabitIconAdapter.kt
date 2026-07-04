package com.example.dailydot.adapter

import com.example.dailydot.data.HabitTemplate
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydot.R
import com.example.dailydot.utils.HabitIconStyler

class HabitIconAdapter(
    private val allItems: List<HabitTemplate>,
    private val onSelected: (HabitTemplate) -> Unit
) : RecyclerView.Adapter<HabitIconAdapter.IconViewHolder>() {

    private val items = allItems.toMutableList()

    // Keep selected index, but never trust method `position`
    private var selectedIndex = -1

    inner class IconViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_habit_icon, parent, false)
        ) {

        val ivIconBg: ImageView = itemView.findViewById(R.id.ivIconBg)
        val tvName: TextView = itemView.findViewById(R.id.tvIconName)
        val radio: RadioButton = itemView.findViewById(R.id.radioSelect)

        fun bind(item: HabitTemplate, isSelected: Boolean) {
            tvName.text = item.name
            HabitIconStyler.apply(ivIconBg, item.iconRes, item.colorRes)

            radio.isChecked = isSelected

            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    selectedIndex = pos              // update selection safely
                    notifyDataSetChanged()
                    onSelected(items[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        IconViewHolder(parent)

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) =
        holder.bind(items[position], position == selectedIndex)

    override fun getItemCount() = items.size

    fun filter(query: String) {
        val normalizedQuery = query.trim()
        items.clear()
        items.addAll(
            if (normalizedQuery.isBlank()) {
                allItems
            } else {
                allItems.filter { it.name.contains(normalizedQuery, ignoreCase = true) }
            }
        )
        selectedIndex = -1
        notifyDataSetChanged()
    }
}
