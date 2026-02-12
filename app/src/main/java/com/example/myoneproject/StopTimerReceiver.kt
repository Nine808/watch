package com.example.myoneproject

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopTimerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Убираем уведомление
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1001)

        // Закрываем Activity
        val closeIntent = Intent(context, TimerExpiredActivity::class.java)
        closeIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        context.startActivity(closeIntent)
    }
}




