package com.example.moodnote.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.util.Calendar

class DateTimePickerHelper(
    private val context: Context,
    private val onDateTimeSelected: (String) -> Unit
) : TimePickerDialog.OnTimeSetListener {

    private lateinit var currentDate: String
    private var minDate: Long? = null

    fun show(minDate: Long? = null) {
        this.minDate = minDate
        showDatePicker()
    }

    private fun showDatePicker() {
        val dateDialog = DatePickerDialog(context).apply {
            minDate?.let { datePicker.minDate = it }

            setOnDateSetListener { _, year, month, day ->
                currentDate = "%04d/%02d/%02d".format(year, month + 1, day)
                showTimePicker()
            }
        }
        dateDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            this,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        var dateStr = "%02d:%02d".format(hour,minute)
        val dateTime = "$currentDate $dateStr"
        onDateTimeSelected(dateTime)
    }
}