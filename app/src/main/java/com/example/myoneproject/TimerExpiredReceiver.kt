package com.example.myoneproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat




    class TimerExpiredReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val fullScreenIntent = Intent(context, TimerExpiredActivity::class.java)

            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, "timer_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Таймер")
                .setContentText("Время вышло")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(2001, notification)
        }

    }

