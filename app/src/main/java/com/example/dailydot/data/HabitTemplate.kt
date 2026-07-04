package com.example.dailydot.data


import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class HabitTemplate(
    val name: String,
    @DrawableRes val iconRes: Int,
    @ColorRes val colorRes: Int
)
