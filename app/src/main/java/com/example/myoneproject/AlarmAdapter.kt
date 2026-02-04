package com.example.myoneproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SwitchCompat

class AlarmAdapter(
    private val alarms: MutableList<AlarmItem>,
    private val onDelete: (AlarmItem) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.alarm_time)
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
        holder.switch.isChecked = alarm.enabled

        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = alarm.enabled

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            alarm.enabled = isChecked
        }

        holder.deleteButton.setOnClickListener {
            onDelete(alarm)
        }
    }

    override fun getItemCount(): Int = alarms.size
}