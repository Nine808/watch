package com.example.myoneproject

data class AlarmItem(
    val id: Int,
    val time: String,
    var enabled: Boolean,   // 👈 ВАЖНО: var
    val daysOfWeek: MutableSet<Int>

)




