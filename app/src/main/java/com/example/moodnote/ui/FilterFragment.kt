package com.example.moodnote.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.moodnote.R
import com.example.moodnote.databinding.FragmentFilterBinding
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import com.google.android.material.transition.platform.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FilterFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var binding: FragmentFilterBinding
    private lateinit var currentButton: Button
    private val allEmotionsElementName: String = "Все эмоции"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAccordion()
        initSpinner()
        initButtons()
        applyFilter(false)
    }

    private fun initAccordion() {
        binding.filterContent.visibility = View.GONE

        binding.accordionButton.setOnClickListener {
            val isExpanded = binding.filterContent.visibility == View.VISIBLE
            TransitionManager.beginDelayedTransition(binding.root, MaterialFadeThrough())
            binding.filterContent.visibility = if (isExpanded) View.GONE else View.VISIBLE
            binding.accordionButton.icon = ContextCompat.getDrawable(
                requireContext(),
                if (isExpanded) R.drawable.baseline_expand_more_24 else
                    R.drawable.baseline_expand_less_24
            )
        }
    }

    override fun onResume() {
        super.onResume()
        applyFilter(false)
    }

    private fun applyFilter(withToast: Boolean = true) {
        viewModel.updateNotes(
            binding.dateFromButton.text.toString().toLongDate(),
            binding.dateToButton.text.toString().toLongDate(),
            getEmotionFromSpinner(),
            binding.eventEditText.text.toString()
        )
        if (withToast) {
            Toast.makeText(requireContext(), "Фильтр применен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initButtons() {
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

        binding.applyButton.setOnClickListener {
            applyFilter()
        }

        binding.clearFilterButton.setOnClickListener {
            viewModel.clearFilter()
            binding.dateFromButton.text = ""
            binding.dateToButton.text = ""
            binding.eventEditText.setText("")
            binding.emotionSpinner.setSelection(0)
            Toast.makeText(requireContext(), "Фильтр сброшен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSpinner() {
        viewModel.emotions.onEach { emotions ->
            val items = mutableListOf(allEmotionsElementName).apply {
                addAll(emotions.map { "${it.getEmojiFromUnicode()} ${it.name}" })
            }
            val adapter = ArrayAdapter(
                requireContext(),
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                items
            )
            adapter.setDropDownViewResource(
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item
            )
            binding.emotionSpinner.adapter = adapter
        }.launchIn(lifecycleScope)
    }

    private fun getEmotionFromSpinner(): Int? {
        val position = binding.emotionSpinner.selectedItemPosition

        if (position == 0) {
            return null
        } else {
            return position
        }
    }

    override fun onTimeSet(p0: TimePicker?, h: Int, m: Int) {
        val time = "$h:$m"
        currentButton.text = "${currentButton.text} $time"
    }
}