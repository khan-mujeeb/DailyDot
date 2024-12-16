package com.example.dailydot.adapter

import android.view.View
import com.example.dailydot.databinding.CalendarDayLayoutBinding
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {

    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    val markerView = CalendarDayLayoutBinding.bind(view).markerView
}