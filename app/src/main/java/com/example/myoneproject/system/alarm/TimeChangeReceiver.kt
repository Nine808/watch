package com.example.myoneproject.system.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myoneproject.AlarmStorage

class TimeChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val storage = AlarmStorage(context)
        val alarms = storage.load()

        for (alarm in alarms) {
            if (alarm.enabled) {
                AlarmScheduler.schedule(context, alarm)
            }
        }
    }
}