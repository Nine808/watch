package com.example.myoneproject

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmStorage(context: Context) {

    private val prefs =
        context.getSharedPreferences("alarms_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()
    private val key = "alarms"

    fun save(alarms: List<AlarmItem>) {
        val json = gson.toJson(alarms)
        prefs.edit().putString(key, json).apply()
    }

    fun load(): MutableList<AlarmItem> {
        val json = prefs.getString(key, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<AlarmItem>>() {}.type
        return gson.fromJson(json, type)
    }
}
