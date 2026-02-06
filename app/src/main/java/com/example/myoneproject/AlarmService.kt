package com.example.myoneproject

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate() {
        super.onCreate()

        startForeground(1, createNotification())

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }
    private fun createNotification(): Notification {

        val channelId = "alarm_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Будильник",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Кнопка "Выключить"
        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Кнопка "Отложить"
        val snoozeIntent = Intent(this, SnoozeReceiver::class.java)
        val snoozePendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Будильник")
            .setContentText("Звонит будильник")
            .addAction(
                R.drawable.ic_delete,
                "Выключить",
                stopPendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Отложить на 10 мин",
                snoozePendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }




    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
