package com.example.myoneproject

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.TimePickerDialog
import java.util.Calendar

class AlarmFragment : Fragment() {

    private val alarms = mutableListOf<AlarmItem>()
    private lateinit var adapter: AlarmAdapter
    private var nextId = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.alarm_list)
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_alarm)

        adapter = AlarmAdapter(alarms) { alarm ->
            alarms.remove(alarm)
            adapter.notifyDataSetChanged()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        addButton.setOnClickListener {
            showTimePicker()
        }


        return view
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                alarms.add(AlarmItem(nextId++, time))
                adapter.notifyDataSetChanged()

            },
            hour,
            minute,
            true // 24-часовой формат
        ).show()
    }
}
