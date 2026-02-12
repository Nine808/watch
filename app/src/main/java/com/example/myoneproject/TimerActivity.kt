package com.example.myoneproject

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftMillis: Long = 0L
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        // ⏱ СТАРТОВОЕ ВРЕМЯ — 2 МИНУТЫ
        timeLeftMillis = 2 * 60 * 1000
        updateTimerUI()

        startButton.setOnClickListener {
            if (!isRunning && timeLeftMillis > 0) {
                startTimer()
            }
        }

        pauseButton.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else if (timeLeftMillis > 0) {
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                isRunning = false
                timeLeftMillis = 0
                updateTimerUI()
                Toast.makeText(this@TimerActivity, "⏰ Таймер!", Toast.LENGTH_SHORT).show()
            }
        }.start()

        isRunning = true
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isRunning = false
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftMillis = 2 * 60 * 1000
        isRunning = false
        updateTimerUI()
    }

    private fun updateTimerUI() {
        val seconds = (timeLeftMillis / 1000) % 60
        val minutes = (timeLeftMillis / 1000) / 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }
}