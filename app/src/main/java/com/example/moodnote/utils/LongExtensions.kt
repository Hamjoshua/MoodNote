package com.example.moodnote.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDateString() : String {
    val date = Date(this)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
    return format.format(date)
}

fun Long.toDateString(pattern: String) : String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern, Locale.getDefault())
    return format.format(date)
}