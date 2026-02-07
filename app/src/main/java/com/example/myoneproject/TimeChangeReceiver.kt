package com.example.myoneproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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



