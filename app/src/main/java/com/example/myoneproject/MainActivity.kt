package com.example.myoneproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myoneproject.ui.alarm.AlarmFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button



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
    private fun createTimerChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel",
                "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for timer"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}