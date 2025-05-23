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
import androidx.fragment.app.viewModels
import com.example.moodnote.R
import com.example.moodnote.databinding.FragmentFilterBinding
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FilterFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private val viewModel : MoodViewModel by viewModels()
    private lateinit var binding: FragmentFilterBinding
    private lateinit var currentButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                minDate = binding.dateFromButton.text.toString().toLongDate()
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

        binding.clearFilterButton.setOnClickListener {
            viewModel.clearFilter()
            binding.dateFromButton.text = ""
            binding.dateToButton.text = ""
            binding.eventEditText.setText("")
        }
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        val time = "$h:$m"
        currentButton.text = "${currentButton.text} $time"
    }
}