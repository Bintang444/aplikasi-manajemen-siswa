package com.example.tugaspkl.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tugaspkl.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Tampilkan Dashboard pertama kali
        loadFragment(DashboardFragment())

        var lastClickTime = 0L

        bottomNav.setOnItemSelectedListener { item ->
            if (System.currentTimeMillis() - lastClickTime < 400) return@setOnItemSelectedListener false
            lastClickTime = System.currentTimeMillis()

            when (item.itemId) {
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_siswa -> loadFragment(SiswaFragment())
                R.id.nav_jurusan -> loadFragment(JurusanFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitAllowingStateLoss()
    }

    fun switchTab(tabId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = tabId
    }
}
