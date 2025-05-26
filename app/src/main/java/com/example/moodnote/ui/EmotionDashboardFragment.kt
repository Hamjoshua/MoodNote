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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.moodnote.R
import com.example.moodnote.databinding.FragmentEmotionDashboardBinding
import com.example.moodnote.databinding.FragmentFilterBinding
import com.example.moodnote.utils.toLongDate
import com.example.moodnote.vm.MoodViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmotionDashboardFragment : Fragment() {
    private lateinit var binding: FragmentEmotionDashboardBinding
    private lateinit var emotionBarChart: BarChart,
    private lateinit var recentEmotionsAdapter: RecentEmotionsAdapter

    private val viewModel: MoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка графика
        emotionBarChart = binding.emotionBarChart
        setupEmotionChart()

        // Загрузка данных
        loadEmotionData()

        // Настройка RecyclerView
        recentEmotionsAdapter = RecentEmotionsAdapter(viewLifecycleOwner,viewModel)
        binding.recentEmotionsList.adapter = recentEmotionsAdapter
    }

    private fun setupEmotionChart() {
        val entries = listOf(
            BarEntry(0f, 5f),  // День 1: 5 радости
            BarEntry(1f, 3f),  // День 2: 3 грусти
            BarEntry(2f, 2f)    // День 3: 2 злости
        )

        val dataSet = BarDataSet(entries, "Emotions").apply {
            color = Color.GREEN
            valueTextColor = Color.WHITE
        }

        val barData = BarData(dataSet)
        emotionBarChart.data = barData
        emotionBarChart.setFitBars(true)
        emotionBarChart.description.isEnabled = false
        emotionBarChart.animateY(1000)
        emotionBarChart.invalidate()
    }

    private fun getRecentEmotions(): List<EmotionData> {
        return listOf(
            EmotionData("Happy", "2023-10-01", "Felt great today!"),
            EmotionData("Sad", "2023-10-02", "Had a rough day..."),
            EmotionData("Angry", "2023-10-03", "Traffic was terrible.")
        )
    }
}