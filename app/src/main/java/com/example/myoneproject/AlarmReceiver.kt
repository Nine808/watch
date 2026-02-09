package com.example.myoneproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("ALARM_DEBUG", "AlarmReceiver triggered")

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        if (alarmId == -1) {
            Log.e("ALARM_DEBUG", "Invalid alarm id")
            return
        }

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID", alarmId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8+
            context.startForegroundService(serviceIntent)
        } else {
            // Android 7 и ниже
            context.startService(serviceIntent)
        }
    }
}



