package com.example.myoneproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Calendar
import java.util.Date

object AlarmScheduler {

    fun schedule(context: Context, alarm: AlarmItem) {

        // ⛔ ВСЕГДА отменяем старый будильник
        cancel(context, alarm)

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 🔐 Android 12+ — разрешение на точные будильники
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return
            }
        }

        val nowMillis = System.currentTimeMillis()

        val (hour, minute) = alarm.time.split(":").map { it.toInt() }

        var nextTrigger: Calendar? = null

        // 🔁 ПОВТОРЯЮЩИЙСЯ ПО ДНЯМ
        if (alarm.daysOfWeek.isNotEmpty()) {

            for (day in alarm.daysOfWeek) {

                val cal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, day)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                    if (timeInMillis <= nowMillis) {
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                }

                if (nextTrigger == null || cal.timeInMillis < nextTrigger.timeInMillis) {
                    nextTrigger = cal
                }
            }

        } else {
            // ⏰ ОДНОРАЗОВЫЙ
            nextTrigger = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (timeInMillis <= nowMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }

        if (nextTrigger == null) return

        val intent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_ID", alarm.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d(
            "ALARM_DEBUG",
            "AlarmId=${alarm.id} Next alarm at ${Date(nextTrigger.timeInMillis)} " +
                    "day=${nextTrigger.get(Calendar.DAY_OF_WEEK)}"
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTrigger.timeInMillis,
            pendingIntent
        )
    }

    fun cancel(context: Context, alarm: AlarmItem) {

        val intent = Intent(context, AlarmActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        Log.d("ALARM_DEBUG", "AlarmId=${alarm.id} canceled")
    }
}

