package com.example.tugaspkl.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tugaspkl.R

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dashboard)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentDashboard, DashboardFragment())
            .commit()
    }
}