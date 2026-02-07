package com.example.myoneproject

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import android.app.AlertDialog
import android.net.Uri


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
         dayMon = view.findViewById<CheckBox>(R.id.day_mon)
        dayTue = view.findViewById<CheckBox>(R.id.day_tue)
         dayWed = view.findViewById<CheckBox>(R.id.day_wed)
         dayThu = view.findViewById<CheckBox>(R.id.day_thu)
         dayFri = view.findViewById<CheckBox>(R.id.day_fri)
         daySat = view.findViewById<CheckBox>(R.id.day_sat)
         daySun = view.findViewById<CheckBox>(R.id.day_sun)

        val recycler = view.findViewById<RecyclerView>(R.id.alarm_list)
        val addButton = view.findViewById<FloatingActionButton>(R.id.add_alarm)

        adapter = AlarmAdapter(
            alarms,
            onDelete = { alarm ->
                alarms.remove(alarm)
                cancelAlarm(alarm)
                storage.save(alarms)
                adapter.notifyDataSetChanged()
            },
            onToggle = { alarm, isEnabled ->

                alarm.enabled = isEnabled
                storage.save(alarms)

                if (isEnabled) {
                    AlarmScheduler.cancel(requireContext(), alarm)
                    AlarmScheduler.schedule(requireContext(), alarm)
                } else {
                    AlarmScheduler.cancel(requireContext(), alarm)
                }
            }
            ,
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

                cancelAlarm(alarm)
                if (alarm.enabled) {
                    scheduleAlarm(alarm)
                }

                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }



    // ---------- Time picker ----------

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

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

                Log.d("ALARM_DAYS", "Выбраны дни: $days")


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

    // ---------- Alarm scheduling ----------

    private fun scheduleAlarm(alarm: AlarmItem) {
        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 🔐 Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
                return
            }
        }

        val now = Calendar.getInstance()
        val parts = alarm.time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        var nextTrigger: Calendar? = null

        // если дни не выбраны → однократно
        if (alarm.daysOfWeek.isEmpty()) {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)

            if (cal.before(now)) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
            }
            nextTrigger = cal
        } else {
            // 🔁 ищем ближайший подходящий день недели
            for (i in 0..6) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, i)
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)

                if (alarm.daysOfWeek.contains(cal.get(Calendar.DAY_OF_WEEK))
                    && cal.after(now)
                ) {
                    nextTrigger = cal
                    break
                }
            }
        }

        if (nextTrigger == null) return

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("ALARM_ID", alarm.id)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTrigger.timeInMillis,
            pendingIntent
        )
    }




    private fun cancelAlarm(alarm: AlarmItem) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }

}
