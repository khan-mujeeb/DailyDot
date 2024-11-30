package com.example.dailydot.adapter

import android.view.View
import android.widget.TextView
import com.example.dailydot.R
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
//     val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
}