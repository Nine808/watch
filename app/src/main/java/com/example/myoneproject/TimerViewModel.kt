package com.example.myoneproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TimerViewModel : ViewModel() {

    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private var endTime: Long = 0L
    private var timerJob: Job? = null

    fun startTimer(duration: Long) {
        endTime = System.currentTimeMillis() + duration

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= 0) {
                    _timeLeft.value = 0
                    break
                }
                _timeLeft.value = remaining
                delay(1000)
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timeLeft.value = 0
    }
}

