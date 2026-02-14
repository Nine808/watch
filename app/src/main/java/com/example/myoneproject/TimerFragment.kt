package com.example.myoneproject

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myoneproject.databinding.FragmentTimerBinding



class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private var selectedDuration: Long = 0L
    private var currentState = TimerForegroundService.TimerState.IDLE


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Подписка на состояние таймера
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            TimerForegroundService.timerStateFlow.collect { state ->
                currentState = state
            }
        }

        _binding = FragmentTimerBinding.bind(view)

        // Выбор времени
        binding.timerTextView.setOnClickListener {
            showTimePicker()
        }

        // КНОПКА STOP
        binding.stopButton.setOnClickListener {

            val intent = Intent(requireContext(), TimerForegroundService::class.java)
            intent.action = TimerForegroundService.ACTION_STOP

            requireContext().startService(intent)

        }



        // Подписка на время из Service
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            TimerForegroundService.timeLeft.collect { time ->
                val totalSeconds = time / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                binding.timerTextView.text =
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }

        }

        // Старт
        binding.pauseButton.setOnClickListener {

            val intent = Intent(requireContext(), TimerForegroundService::class.java)

            when (currentState) {

                TimerForegroundService.TimerState.IDLE -> {
                    intent.action = TimerForegroundService.ACTION_START
                    intent.putExtra("duration", selectedDuration)
                }

                TimerForegroundService.TimerState.RUNNING -> {
                    intent.action = TimerForegroundService.ACTION_PAUSE
                }

                TimerForegroundService.TimerState.PAUSED -> {
                    intent.action = TimerForegroundService.ACTION_START
                }
            }

            ContextCompat.startForegroundService(requireContext(), intent)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showTimePicker() {

        val picker = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->

                selectedDuration =
                    (hourOfDay * 3600 + minute * 60) * 1000L

                // Обновляем текст на экране
                updateTimeText(selectedDuration)
            },
            0,
            0,
            true
        )

        picker.show()
    }
    private fun updateTimeText(time: Long) {

        val totalSeconds = time / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        binding.timerTextView.text =
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}








