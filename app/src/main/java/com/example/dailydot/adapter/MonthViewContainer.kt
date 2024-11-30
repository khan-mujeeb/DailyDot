package com.example.dailydot.adapter

import android.view.View
import android.widget.TextView
import com.example.dailydot.R
import com.kizitonwose.calendar.view.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textView: TextView = view.findViewById(R.id.monthHeaderText)
}

