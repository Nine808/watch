package com.example.myoneproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // стартовый экран
        openFragment(AlarmFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_alarm -> {
                    openFragment(AlarmFragment())
                    true
                }
                R.id.menu_timer -> {
                    openFragment(TimerFragment())
                    true
                }
                R.id.menu_stopwatch -> {
                    openFragment(StopwatchFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}