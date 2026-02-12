package com.example.myoneproject

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class TimerFragment : Fragment(R.layout.fragment_timer) {

    private lateinit var timerTextView: TextView
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton

    private var timer: CountDownTimer? = null
    private var timeLeftMillis: Long = 0L
    private var isRunning = false
    private var triggerTime: Long = 0L
    private var pendingIntent: PendingIntent? = null
    private var mediaPlayer: android.media.MediaPlayer? = null


    private val CHANNEL_ID = "TIMER_CHANNEL"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannel()
        requestNotificationPermissionIfNeeded()

        timerTextView = view.findViewById(R.id.timerTextView)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)

        updateUI()

        pauseButton.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer()
        }

        stopButton.setOnClickListener {
            resetTimer()
        }

        timerTextView.setOnClickListener {
            if (!isRunning) showTimePicker()
        }
    }

    // --------------------------------------------------
    // TIMER
    // --------------------------------------------------

    private fun startTimer() {

        if (timeLeftMillis <= 0) return

        timer = object : CountDownTimer(timeLeftMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateUI()
            }

            override fun onFinish() {
                isRunning = false
                timeLeftMillis = 0
                updateUI()

                TimerSoundPlayer.start(requireContext())

                showFullScreenNotification()
            }



        }.start()

        isRunning = true
    }




    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    private fun resetTimer() {
        timer?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        timeLeftMillis = 0
        isRunning = false
        updateUI()
    }


    // --------------------------------------------------
    // FULL SCREEN NOTIFICATION
    // --------------------------------------------------

    private fun showFullScreenNotification() {

        val context = requireContext()

        val fullScreenIntent = Intent(context, TimerExpiredActivity::class.java)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            2000,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, StopTimerReceiver::class.java)

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Таймер")
            .setContentText("Время вышло")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

            .addAction(
                R.drawable.ic_stop,
                "Отключить",
                stopPendingIntent
            )
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    // --------------------------------------------------
    // NOTIFICATION CHANNEL
    // --------------------------------------------------

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timer notifications"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val manager =
                requireContext().getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    // --------------------------------------------------
    // PERMISSION (Android 13+)
    // --------------------------------------------------

    private fun requestNotificationPermissionIfNeeded() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    // --------------------------------------------------
    // TIME PICKER
    // --------------------------------------------------

    private fun showTimePicker() {

        val totalMinutes = (timeLeftMillis / 60000).toInt()
        val currentHours = totalMinutes / 60
        val currentMinutes = totalMinutes % 60

        val picker = android.app.TimePickerDialog(
            requireContext(),
            { _, selectedHours, selectedMinutes ->
                timeLeftMillis =
                    (selectedHours * 60L + selectedMinutes) * 60_000L
                updateUI()
            },
            currentHours,
            currentMinutes,
            true
        )

        picker.show()
    }

    // --------------------------------------------------
    // UI UPDATE
    // --------------------------------------------------

    private fun updateUI() {

        val totalSeconds = timeLeftMillis / 1000

        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        timerTextView.text = String.format(
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds
        )
    }
}




