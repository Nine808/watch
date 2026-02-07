package com.example.myoneproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("ALARM_DEBUG", "AlarmReceiver triggered")

        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        Log.d("ALARM_DEBUG", "Alarm ID = $alarmId")

        if (alarmId == -1) return

        val storage = AlarmStorage(context)
        val alarm = storage.load().find { it.id == alarmId }

        if (alarm == null) {
            Log.e("ALARM_DEBUG", "Alarm not found")
            return
        }

        // 🔔 запускаем экран будильника
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_ID", alarm.id)
            putExtra("TIME", alarm.time)
        }

        context.startActivity(activityIntent)

        // 🔁 ПЕРЕПЛАНИРОВАНИЕ
        if (alarm.enabled) {
            AlarmScheduler.schedule(context, alarm)
            Log.d("ALARM_DEBUG", "Alarm rescheduled")
        }
    }

}

