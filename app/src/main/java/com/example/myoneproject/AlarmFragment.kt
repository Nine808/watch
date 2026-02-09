package com.example.myoneproject

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import android.app.AlertDialog

class AlarmFragment : Fragment() {

    private lateinit var storage: AlarmStorage
    private val alarms = mutableListOf<AlarmItem>()
    private lateinit var adapter: AlarmAdapter

    private lateinit var dayMon: CheckBox
    private lateinit var dayTue: CheckBox
    private lateinit var dayWed: CheckBox
    private lateinit var dayThu: CheckBox
    private lateinit var dayFri: CheckBox
    private lateinit var daySat: CheckBox
    private lateinit var daySun: CheckBox

    // ---------- UI ----------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        storage = AlarmStorage(requireContext())

        val view = inflater.inflate(R.layout.fragment_alarm, container, false)

        dayMon = view.findViewById(R.id.day_mon)
        dayTue = view.findViewById(R.id.day_tue)
        dayWed = view.findViewById(R.id.day_wed)
        dayThu = view.findViewById(R.id.day_thu)
        dayFri = view.findViewById(R.id.day_fri)
        daySat = view.findViewById(R.id.day_sat)
        daySun = view.findViewById(R.id.day_sun)

        val recycler = view.findViewById<RecyclerView>(R.id.alarm_list)
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_alarm)

        adapter = AlarmAdapter(
            alarms,
            onDelete = { alarm ->
                AlarmScheduler.cancel(requireContext(), alarm)
                alarms.remove(alarm)
                storage.save(alarms)
                adapter.notifyDataSetChanged()
            },
            onToggle = { alarm, isEnabled ->
                alarm.enabled = isEnabled
                storage.save(alarms)

                if (isEnabled) {
                    AlarmScheduler.schedule(requireContext(), alarm)
                } else {
                    AlarmScheduler.cancel(requireContext(), alarm)
                }
            },
            onEdit = { alarm ->
                editAlarmDays(alarm)
            }
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        addButton.setOnClickListener {
            showTimePicker()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarms.clear()
        alarms.addAll(storage.load())
        adapter.notifyDataSetChanged()
    }

    // ---------- Редактирование дней ----------

    private fun editAlarmDays(alarm: AlarmItem) {

        val dayNames = arrayOf(
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота",
            "Воскресенье"
        )

        val dayValues = arrayOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
        )

        val checked = BooleanArray(7) { i ->
            alarm.daysOfWeek.contains(dayValues[i])
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Дни недели")
            .setMultiChoiceItems(dayNames, checked) { _, index, isChecked ->
                if (isChecked) {
                    alarm.daysOfWeek.add(dayValues[index])
                } else {
                    alarm.daysOfWeek.remove(dayValues[index])
                }
            }
            .setPositiveButton("ОК") { _, _ ->
                storage.save(alarms)

                AlarmScheduler.cancel(requireContext(), alarm)
                if (alarm.enabled) {
                    AlarmScheduler.schedule(requireContext(), alarm)
                }

                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // ---------- Добавление будильника ----------

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val dialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->

                val timeString =
                    String.format("%02d:%02d", selectedHour, selectedMinute)

                val days = mutableSetOf<Int>()

                if (dayMon.isChecked) days.add(Calendar.MONDAY)
                if (dayTue.isChecked) days.add(Calendar.TUESDAY)
                if (dayWed.isChecked) days.add(Calendar.WEDNESDAY)
                if (dayThu.isChecked) days.add(Calendar.THURSDAY)
                if (dayFri.isChecked) days.add(Calendar.FRIDAY)
                if (daySat.isChecked) days.add(Calendar.SATURDAY)
                if (daySun.isChecked) days.add(Calendar.SUNDAY)

                Log.d("ALARM_DEBUG", "Создание будильника, дни=$days")

                val newAlarm = AlarmItem(
                    id = System.currentTimeMillis().toInt(),
                    time = timeString,
                    enabled = true,
                    daysOfWeek = days
                )

                alarms.add(newAlarm)
                storage.save(alarms)
                adapter.notifyDataSetChanged()

                AlarmScheduler.schedule(requireContext(), newAlarm)

            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        dialog.show()
    }
}
