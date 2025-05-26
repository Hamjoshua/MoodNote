package com.example.moodnote.utils

import java.text.SimpleDateFormat
import java.util.Date

fun Long.toDateString() : String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return format.format(date)
}