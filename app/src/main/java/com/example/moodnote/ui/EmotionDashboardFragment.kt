package com.example.moodnote.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.moodnote.data.Emotion
import com.example.moodnote.databinding.FragmentEmotionDashboardBinding
import com.example.moodnote.vm.ExtendedMoodViewModel
import com.example.moodnote.vm.MoodViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.util.Calendar

class EmotionDashboardFragment : Fragment() {
    private lateinit var binding: FragmentEmotionDashboardBinding
//    private lateinit var emotionBarChart: BarChart,
    private lateinit var recentEmotionsAdapter: RecentEmotionsAdapter
    private lateinit var emotionCalendarAdapter: EmotionCalendarAdapter

    private val viewModel: ExtendedMoodViewModel by activityViewModels()
    private lateinit var emotionBarChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getEmojiFromUnicode(emotion: Emotion): String {
        return String(Character.toChars(emotion.emojiUnicode))
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

        emotionCalendarAdapter = EmotionCalendarAdapter(view.context, emptyList())
        binding.emotionCalendarGrid.adapter = emotionCalendarAdapter

        val calendar = Calendar.getInstance()
        val dateTo = calendar.timeInMillis
        // Subtract one month to get the start date (dateFrom)
        calendar.add(Calendar.MONTH, -1)
        val dateFrom = calendar.timeInMillis
        val emotionIdList: List<Int>? = emptyList() // or emptyList(), depending on what you want
        val event: String? = null

        viewModel.getDistinctEmotionIdsByFilter(dateFrom,dateTo,emotionIdList,event) { emotionIds ->
            val listEmotions = viewModel.emotions.value ?: emptyList()
            val emojiList: List<String> = emotionIds.mapNotNull { id ->
                listEmotions.find { it.id == id }?.let { emotion ->
                    getEmojiFromUnicode(emotion)
                }
            }
            emotionCalendarAdapter.setData(emojiList)
        }
    }

    private fun loadEmotionData() {
        return
        // реализация логики загрзуки
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