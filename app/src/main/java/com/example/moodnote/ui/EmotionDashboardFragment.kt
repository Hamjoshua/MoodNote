package com.example.moodnote.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.moodnote.data.Emotion
import com.example.moodnote.databinding.FragmentEmotionDashboardBinding
import com.example.moodnote.vm.ExtendedMoodViewModel
import com.example.moodnote.vm.MoodViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

data class EmotionStat(
    val emoji: String,  // Unicode эмодзи
    val count: Int,       // Количество записей
//    val name: String      // Название эмоции (например "Радость")
)

class EmotionDashboardFragment : Fragment() {
    private val viewModel: ExtendedMoodViewModel by activityViewModels()
    private lateinit var binding: FragmentEmotionDashboardBinding
    private lateinit var recentEmotionsAdapter: RecentEmotionsAdapter
    private lateinit var emotionCalendarAdapter: EmotionCalendarAdapter
    private lateinit var emotionBarChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getEmojiFromUnicode(emojiUnicode: Int): String {
        return String(Character.toChars(emojiUnicode))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка графика
        emotionBarChart = binding.emotionBarChart

        var emotionStats: List<EmotionStat>

        // Загрузка данных
        loadEmotionData()

        // Настройка RecyclerView
        recentEmotionsAdapter = RecentEmotionsAdapter(viewLifecycleOwner,viewModel)
        binding.recentEmotionsList.adapter = recentEmotionsAdapter

        emotionCalendarAdapter = EmotionCalendarAdapter(view.context, emptyList())
        binding.emotionCalendarGrid.adapter = emotionCalendarAdapter

        val calendar = Calendar.getInstance()
        val dateTo = calendar.timeInMillis

        calendar.add(Calendar.MONTH, -1)
        val dateFrom = calendar.timeInMillis
        val emotionIdList: List<Int>? = emptyList()
        val event: String? = null

        viewModel.getDistinctEmotionEmojiCodeIdsByFilter(dateFrom,dateTo,emotionIdList,event) { emojiCodes ->
            val emojiList: List<String> = emojiCodes.mapNotNull { code ->
                getEmojiFromUnicode(code)
            }

            emotionCalendarAdapter.setData(emojiList)
        }

        viewModel.getDistinctEmotionEmojiCodeIdsByFilter(dateFrom,dateTo,emotionIdList,event) { emojiCodes ->
            val emojiList: List<String> = emojiCodes.mapNotNull { code ->
                getEmojiFromUnicode(code)
            }

            emotionCalendarAdapter.setData(emojiList)
        }

        viewModel.getEmotionEmojiCodeCountIdsByFilter(dateFrom, dateTo, emotionIdList, event) { emojiCodesCount ->
            val emojiList = emojiCodesCount.map { (emojiCode, count) ->
                EmotionStat(
                    emoji = getEmojiFromUnicode(emojiCode),
                    count = count
                )
            }
            setupEmotionChart(emojiList)
        }
    }

    private fun loadEmotionData() {
        return
        // реализация логики загрзуки
    }

//    private fun setupEmotionChart() {
//        val entries = listOf(
//            BarEntry(0f, 5f),  // День 1: 5 радости
//            BarEntry(1f, 3f),  // День 2: 3 грусти
//            BarEntry(2f, 2f)    // День 3: 2 злости
//        )
//
//        val dataSet = BarDataSet(entries, "Emotions").apply {
//            color = Color.GREEN
//            valueTextColor = Color.WHITE
//        }
//
//        val barData = BarData(dataSet)
//        emotionBarChart.data = barData
//        emotionBarChart.setFitBars(true)
//        emotionBarChart.description.isEnabled = false
//        emotionBarChart.animateY(1000)
//        emotionBarChart.invalidate()
//    }

    private fun getColorForEmotion(emoj: String): Int {
        return when(emoj) {
            "\uD83D\uDE00" -> Color.YELLOW    // 😀
            "\uD83D\uDE22" -> Color.BLUE      // 😢
            "\uD83D\uDE20" -> Color.RED       // 😠
            else -> Color.GRAY
        }
    }

    private fun setupEmotionChart(emotionStats: List<EmotionStat>) {
        if (emotionStats.isEmpty()) {
            emotionBarChart.clear()
            emotionBarChart.invalidate()
            return
        }

        // 1. Подготовка данных
        val entries = emotionStats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.count.toFloat())
        }

        // 2. Создание набора данных
        val dataSet = BarDataSet(entries, "Частота эмоций").apply {
//            colors = emotionStats.map { it) }
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawValues(true)
        }
//
//        // 3. Настройка графика
//        emotionBarChart.apply {
//            data = BarData(dataSet)
//
//            // Настройка оси X с эмодзи
//            xAxis.apply {
//                valueFormatter = object : ValueFormatter() {
//                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                        val index = value.toInt()
//                        return if (index in emotionStats.indices) {
//                            emotionStats[index].emoji
//                        } else ""
//                    }
//                }
//                position = XAxis.XAxisPosition.BOTTOM
//                granularity = 1f
//                setDrawGridLines(false)
//            }
//
//            // Настройка оси Y
//            axisLeft.apply {
//                granularity = 1f
//                axisMinimum = 0f
//            }
//            axisRight.isEnabled = false
//
//            // Общие настройки
//            legend.isEnabled = false
//            description.isEnabled = false
//            setTouchEnabled(false)
//            setFitBars(true)
//            animateY(1000)
//            invalidate()
//        }

        emotionBarChart.apply {
            // Все настройки данных и осей
            data = BarData(dataSet)

            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        val index = value.toInt()
                        return if (index in emotionStats.indices) emotionStats[index].emoji else ""
                    }
                }
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                granularity = 1f
                axisMinimum = 0f
            }
            axisRight.isEnabled = false

            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)
            setFitBars(true)

            // Запускаем анимацию в UI-потоке
            post {
                animateY(1000)
                invalidate()
            }
        }

    }
}