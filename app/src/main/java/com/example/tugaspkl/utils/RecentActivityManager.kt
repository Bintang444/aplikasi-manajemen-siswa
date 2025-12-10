package com.example.tugaspkl.utils

import com.example.tugaspkl.model.RecentActivity

object RecentActivityManager {
    val activityList = mutableListOf<RecentActivity>()

    fun addActivity(title: String) {
        val date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
        activityList.add(0, RecentActivity(title, date))
    }
}