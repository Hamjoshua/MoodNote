package com.example.moodnote.utils

import java.text.SimpleDateFormat

fun String.toLongDate() : Long {
    val formatedDate = SimpleDateFormat("yyyy/MM/dd HH:mm").parse(
        this
    )

    return formatedDate.time
}