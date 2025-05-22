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
            if (binding.dateFromButton.text != "") {
                val date = SimpleDateFormat("yyyy/MM/dd").parse(
                    binding.dateFromButton.text.toString()
                )
                minDate = date.time
            }

            DateTimePickerHelper(requireContext()) {
                binding.dateToButton.text = it
            }.show(minDate)
        }

        binding.dateFromButton.setOnClickListener {
            DateTimePickerHelper(requireContext()) {
                binding.dateFromButton.text = it
            }.show()

        }
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        val time = "$h:$m"
        currentButton.text = "${currentButton.text} $time"
    }
}