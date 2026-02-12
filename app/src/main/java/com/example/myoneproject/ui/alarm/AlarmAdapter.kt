package com.example.myoneproject.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myoneproject.AlarmItem
import com.example.myoneproject.R
import java.util.Calendar

class AlarmAdapter(
    private val alarms: MutableList<AlarmItem>,
    private val onDelete: (AlarmItem) -> Unit,
    private val onToggle: (AlarmItem, Boolean) -> Unit,
    private val onEdit: (AlarmItem) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.alarm_time)
        val daysText: TextView = view.findViewById(R.id.alarm_days) // ← ДОБАВИЛИ
        val deleteButton: ImageButton = view.findViewById(R.id.delete_button)
        val switch: SwitchCompat = view.findViewById(R.id.alarm_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]

        holder.timeText.text = alarm.time
        holder.daysText.text = formatDays(alarm.daysOfWeek) // ← ВОТ ОНО
        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = alarm.enabled

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            alarm.enabled = isChecked
            onToggle(alarm, isChecked)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(alarm)
        }
        holder.itemView.setOnClickListener {
            onEdit(alarm)
        }

    }

    override fun getItemCount(): Int = alarms.size

    // ====== форматирование дней недели ======
    private fun formatDays(days: Set<Int>): String {
        if (days.isEmpty()) return "Однократно"

        val map = mapOf(
            Calendar.MONDAY to "Пн",
            Calendar.TUESDAY to "Вт",
            Calendar.WEDNESDAY to "Ср",
            Calendar.THURSDAY to "Чт",
            Calendar.FRIDAY to "Пт",
            Calendar.SATURDAY to "Сб",
            Calendar.SUNDAY to "Вс"
        )

        return days
            .sorted()
            .mapNotNull { map[it] }
            .joinToString(" ")
    }
}