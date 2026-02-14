package com.example.myoneproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow


class TimerForegroundService : Service() {
    // 🔥 Состояния таймера
   enum class TimerState {
        IDLE, RUNNING, PAUSED
    }

    private var timerState = TimerState.IDLE
    private var remainingTime: Long = 0L


    private var endTime: Long = 0L
    private var timerJob: Job? = null
    private var isFinished = false
    private var isTimerActive = false

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        const val CHANNEL_ID = "TIMER_CHANNEL"
        const val ACTION_PAUSE = "ACTION_PAUSE"


        val timeLeft = MutableStateFlow(0L)
        val timerStateFlow = MutableStateFlow(TimerState.IDLE)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {

            ACTION_STOP -> {

                Log.d("TIMER_DEBUG", "STOP pressed")

                timerJob?.cancel()
                timerJob = null

                timerState = TimerState.IDLE
                remainingTime = 0L
                timeLeft.value = 0L
                isFinished = false

                TimerSoundPlayer.stop()

                // Убираем finished уведомление (id = 2)
                val manager = getSystemService(NotificationManager::class.java)
                manager.cancel(2)

                // Убираем foreground уведомление
                stopForeground(STOP_FOREGROUND_REMOVE)

                stopSelf()

                return START_NOT_STICKY
            }


            ACTION_START -> {

                when (timerState) {

                    TimerState.IDLE -> {

                        val duration = intent.getLongExtra("duration", 0L)

                        if (duration > 0) {
                            remainingTime = duration
                            startTimer()
                            startForegroundServiceNotification()
                        }
                    }

                    TimerState.RUNNING -> {
                        // Это теперь пауза
                        timerState = TimerState.PAUSED
                        timerStateFlow.value = TimerState.PAUSED
                        timerJob?.cancel()
                    }

                    TimerState.PAUSED -> {
                        // Это возобновление
                        startTimer()
                    }
                }
            }
        }




            startForegroundServiceNotification()
        return START_STICKY
    }


    private fun startTimer() {

        if (timerState == TimerState.RUNNING) return

        timerState = TimerState.RUNNING

        timerJob?.cancel()

        timerJob = CoroutineScope(Dispatchers.Default).launch {

            while (remainingTime > 0 && timerState == TimerState.RUNNING) {

                delay(1000)

                remainingTime -= 1000
                timeLeft.value = remainingTime
            }

            if (remainingTime <= 0 && timerState == TimerState.RUNNING) {
                timerState = TimerState.IDLE
                onTimerFinished()
            }
        }
    }



    private fun startForegroundServiceNotification() {

        val channelId = "TIMER_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer",
                NotificationManager.IMPORTANCE_HIGH   // 🔥 ВАЖНО
            ).apply {
                description = "Таймер в работе"
                enableVibration(true)
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Таймер работает")
            .setContentText("Идёт отсчёт времени")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun onTimerFinished() {
        if (isFinished) return   // 🔥 защита от повторного вызова
        isFinished = true

        // 🔊 Запускаем звук
        TimerSoundPlayer.start(this)

        // 🚨 Открываем full screen activity
        val intent = Intent(this, TimerExpiredActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // 🛑 Обновляем уведомление
        showFinishedNotification()
    }

    private fun showFinishedNotification() {

        createNotificationChannel()

        val stopIntent = Intent(this, TimerForegroundService::class.java)
        stopIntent.action = ACTION_STOP

        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullScreenIntent = Intent(this, TimerExpiredActivity::class.java)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер завершён")
            .setContentText("Нажмите, чтобы остановить")
            .setSmallIcon(R.drawable.ic_stop)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(R.drawable.ic_stop, "Остановить", stopPendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, notification)
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}


