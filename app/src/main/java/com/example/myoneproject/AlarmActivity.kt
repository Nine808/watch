package com.example.myoneproject

import android.content.Intent
import android.media.*
import android.net.Uri
import android.os.*
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var alarmId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔓 экран поверх блокировки
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        setContentView(R.layout.activity_alarm)

        alarmId = intent.getIntExtra("ALARM_ID", -1)

        startAlarmSound()
        setupButtons()
    }

    // 🔘 кнопки
    private fun setupButtons() {

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopAlarm()
        }

        findViewById<Button>(R.id.btnSnooze).setOnClickListener {
            snoozeAlarm()
        }
    }

    // 🔔 звук
    private fun startAlarmSound() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )

            setDataSource(
                this@AlarmActivity,
                Uri.parse("android.resource://${packageName}/${R.raw.alarm_sound}")
            )

            isLooping = true
            prepare()
            start()
        }
    }

    // ⛔ выключить
    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        stopService(Intent(this, AlarmService::class.java))
        finish()
    }

    // ⏰ отложить на 10 минут (просто закрываем, планирование у тебя уже есть)
    private fun snoozeAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        stopService(Intent(this, AlarmService::class.java))
        finish()
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}


