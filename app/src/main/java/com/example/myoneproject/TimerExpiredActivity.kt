package com.example.myoneproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TimerExpiredActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    // Receiver для кнопки из уведомления

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_expired)
        TimerSoundPlayer.start(this)

        // показываем поверх lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // Кнопка на экране
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            TimerSoundPlayer.stop()
            finish()
        }

        // Регистрируем receiver для уведомления
        val filter = IntentFilter("STOP_TIMER")



    }

    override fun onDestroy() {
        super.onDestroy()
        TimerSoundPlayer.stop()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        TimerSoundPlayer.stop()
        finish()
    }



}
