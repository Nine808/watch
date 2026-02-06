package com.example.myoneproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlarmFragment : Fragment() {

    private lateinit var storage: AlarmStorage
    private val alarms = mutableListOf<AlarmItem>()
    private lateinit var adapter: AlarmAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.alarm_list)
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_alarm)

        adapter = AlarmAdapter(alarms) { alarm ->
            alarms.remove(alarm)
            storage.save(alarms)
            adapter.notifyDataSetChanged()
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        addButton.setOnClickListener {
            addTestAlarm()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storage = AlarmStorage(requireContext())

        alarms.clear()
        alarms.addAll(storage.load())
        adapter.notifyDataSetChanged()
    }

    private fun addTestAlarm() {
        val newAlarm = AlarmItem(
            id = System.currentTimeMillis().toInt(),
            time = "07:30",
            enabled = true
        )
        alarms.add(newAlarm)
        storage.save(alarms)
        adapter.notifyDataSetChanged()
    }
}

