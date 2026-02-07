package com.example.myoneproject

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔊 ЗАПУСКАЕМ ЗВУК СРАЗУ
        val serviceIntent = Intent(this, AlarmService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }



        // Показываем поверх блокировки
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContentView(R.layout.activity_alarm)

        val timeView = findViewById<TextView>(R.id.alarm_time)
        val stopButton = findViewById<Button>(R.id.btn_stop)
        val snoozeButton = findViewById<Button>(R.id.btn_snooze)

        val time = intent.getStringExtra("TIME") ?: "Будильник"
        timeView.text = time

        // ВЫКЛЮЧИТЬ
        stopButton.setOnClickListener {
            stopService(Intent(this, AlarmService::class.java))
            finish()
        }

        // ОТЛОЖИТЬ НА 10 МИНУТ
        snoozeButton.setOnClickListener {
            stopService(Intent(this, AlarmService::class.java))

            val snoozeIntent = Intent(this, SnoozeReceiver::class.java)
            sendBroadcast(snoozeIntent)

            finish()
        }
    }
}
