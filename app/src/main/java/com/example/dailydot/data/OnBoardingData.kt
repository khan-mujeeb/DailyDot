package com.example.dailydot.data

import com.example.dailydot.R
import com.xcode.onboarding.OnBoardingPage

object OnBoardingData {


    public fun getOnBoardingData(): ArrayList<OnBoardingPage> {

        val pages = ArrayList<OnBoardingPage>()
        pages.add(
            OnBoardingPage(
                R.drawable.image1,

                "Welcome to DailyDot!",
                "Start your journey to building better habits, one day at a time. Track progress effortlessly with a clean and intuitive interface."
            )
        )
        pages.add(
            OnBoardingPage(
                R.drawable.image2,

                "Visualize Your Progress",
                "Use the calendar to monitor your habits and celebrate your streaks. Small daily dots lead to big achievements!"
            )
        )
        pages.add(
            OnBoardingPage(
                R.drawable.image3,

                "Stay Consistent",
                "Set goals, track your habits, and enjoy the satisfaction of achieving your milestones. Your success story begins today!"
            )
        )

        return pages

    }

}