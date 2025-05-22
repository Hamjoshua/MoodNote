package com.example.moodnote.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import com.example.moodnote.R
import com.example.moodnote.databinding.FragmentFilterBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FilterFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentFilterBinding
    private lateinit var currentButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
    }

    private fun initButtons() {
        // TODO
        binding.dateToButton.setOnClickListener {
            var minDate: Long? = null
            binding.dateFromButton.text?.let {
                val date = SimpleDateFormat("yyyy/MM/dd").parse(it.toString())
                minDate = date.time
            }
            currentButton = binding.dateToButton
            initDateTimeDialogs(minDate)
        }

        binding.dateFromButton.setOnClickListener {
            currentButton = binding.dateFromButton
            initDateTimeDialogs()
        }
    }

    private fun initDateTimeDialogs(minDate: Long? = null) {
        var date: String = ""

        val dateDialog: DatePickerDialog = DatePickerDialog(requireContext())
        minDate?.let {
            dateDialog.datePicker.minDate = minDate
        }

        dateDialog.show()

        dateDialog.setOnDateSetListener { datePicker, y, m, d ->
            Log.d("datepicker", "Date set")
            date = "$y/$m/$d"
            currentButton.text = date

            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog: TimePickerDialog =
                TimePickerDialog(
                    requireContext(), this, hour, minute,
                    DateFormat.is24HourFormat(activity)
                )

            timePickerDialog.show()
        }
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        val time = "$h:$m"
        currentButton.text = "${currentButton.text} $time"
    }
}