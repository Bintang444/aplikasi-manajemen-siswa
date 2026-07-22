package com.example.tugaspkl.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tugaspkl.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val dashboardFragment = DashboardFragment()
    private val siswaFragment = SiswaFragment()
    private val jurusanFragment = JurusanFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = dashboardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, dashboardFragment, "dashboard")
                .add(R.id.fragment_container, siswaFragment, "siswa").hide(siswaFragment)
                .add(R.id.fragment_container, jurusanFragment, "jurusan").hide(jurusanFragment)
                .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
                .commit()
        }

        var lastClickTime = 0L

        bottomNav.setOnItemSelectedListener { item ->
            if (System.currentTimeMillis() - lastClickTime < 400) return@setOnItemSelectedListener false
            lastClickTime = System.currentTimeMillis()

            when (item.itemId) {
                R.id.nav_dashboard -> switchToFragment(dashboardFragment)
                R.id.nav_siswa -> switchToFragment(siswaFragment)
                R.id.nav_jurusan -> switchToFragment(jurusanFragment)
                R.id.nav_profile -> switchToFragment(profileFragment)
            }
            true
        }
    }

    private fun switchToFragment(fragment: Fragment) {
        if (fragment == activeFragment) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()
        activeFragment = fragment
    }

    fun switchTab(tabId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = tabId
    }
}
