package com.example.moodnote.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.activityViewModels
import com.example.moodnote.R
import com.example.moodnote.adapters.EmotionCalendarAdapter
import com.example.moodnote.adapters.RecentEmotionsAdapter
import com.example.moodnote.databinding.FragmentEmotionDashboardBinding
import com.example.moodnote.vm.ExtendedMoodViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.Calendar

data class EmotionStat(
    val emoji: String,
    val count: Int,
    val weightCount: Int
)

class EmotionDashboardFragment : Fragment() {
    private val viewModel: ExtendedMoodViewModel by activityViewModels()
    private lateinit var binding: FragmentEmotionDashboardBinding
    private lateinit var recentEmotionsAdapter: RecentEmotionsAdapter
    private lateinit var emotionCalendarAdapter: EmotionCalendarAdapter
    private lateinit var emotionBarChart: BarChart

    private var positiveGraphColor: Int = 0
    private var negativeGraphColor: Int = 0
    private var countGraphColor: Int = 0
    private var graphTextColor: Int = 0
    private var surfaceColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        positiveGraphColor = ContextCompat.getColor(requireContext(), R.color.md_theme_primary)
        negativeGraphColor = ContextCompat.getColor(requireContext(), R.color.md_theme_error)
        countGraphColor = ContextCompat.getColor(requireContext(), R.color.md_theme_tertiary)
        graphTextColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onBackground)
        surfaceColor = ContextCompat.getColor(requireContext(), R.color.md_theme_surface)

        binding = FragmentEmotionDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun getEmojiFromUnicode(emojiUnicode: Int): String {
        return String(Character.toChars(emojiUnicode))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emotionBarChart = binding.emotionBarChart

        recentEmotionsAdapter = RecentEmotionsAdapter(viewModel)
        binding.recentEmotionsList.adapter = recentEmotionsAdapter

        emotionCalendarAdapter = EmotionCalendarAdapter(view.context, emptyList())
        binding.emotionCalendarGrid.adapter = emotionCalendarAdapter

        val calendar = Calendar.getInstance()
        val dateTo = calendar.timeInMillis

        calendar.add(Calendar.MONTH, -1)
        val dateFrom = calendar.timeInMillis
        val emotionIdList: List<Int>? = emptyList()
        val event: String? = null

        viewModel.getDistinctEmotionEmojiCodeIdsByFilter(
            dateFrom,
            dateTo,
            emotionIdList,
            event
        ) { emojiCodes ->
            val emojiList: List<String> = emojiCodes.mapNotNull { code ->
                getEmojiFromUnicode(code)
            }

            emotionCalendarAdapter.setData(emojiList)
        }

        viewModel.getDistinctEmotionEmojiCodeIdsByFilter(
            dateFrom,
            dateTo,
            emotionIdList,
            event
        ) { emojiCodes ->
            val emojiList: List<String> = emojiCodes.mapNotNull { code ->
                getEmojiFromUnicode(code)
            }

            emotionCalendarAdapter.setData(emojiList)
        }

        viewModel.getEmotionEmojiCodeCountIdsByFilter(
            dateFrom,
            dateTo,
            emotionIdList,
            event
        ) { emojiCodesCount ->
            val emojiList = emojiCodesCount.map {
                EmotionStat(
                    emoji = getEmojiFromUnicode(it.emojiUnicode),
                    count = it.count,
                    weightCount = it.weightCount
                )
            }
            setupEmotionChart(emojiList)
        }
    }

    private fun getBarData(emotionStats: List<EmotionStat>): BarData {
        val weightEntries = emotionStats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.weightCount.toFloat())
        }

        val countEntries = emotionStats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.count.toFloat() / 4)
        }

        val weightDataSet = BarDataSet(weightEntries, "Отклик").apply {
            valueTextColor = graphTextColor
            valueTextSize = 12f
            colors = weightEntries.map { entry ->
                if (entry.y >= 0) positiveGraphColor else negativeGraphColor
            }
        }

        val countDataSet = BarDataSet(countEntries, "Кол-во").apply {
            valueTextColor = graphTextColor
            valueTextSize = 12f
            setDrawValues(true)
            color = countGraphColor
        }
        countDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return (barEntry.y * 4).toInt().toString()
            }
        }

        val combinedData = BarData(weightDataSet, countDataSet).apply {
        }

        return combinedData
    }

    private fun setupEmotionChart(emotionStats: List<EmotionStat>) {
        if (emotionStats.isEmpty()) {
            emotionBarChart.clear()
            emotionBarChart.invalidate()
            return
        }

        emotionBarChart.apply {
            data = getBarData(emotionStats)

            xAxis.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                        val index = value.toInt()
                        return if (index in emotionStats.indices) emotionStats[index].emoji else ""
                    }
                }
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                gridColor = surfaceColor
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = graphTextColor
                axisLineColor = graphTextColor
                granularity = 1f
                gridColor = graphTextColor
            }
            axisRight.apply {
                textColor = graphTextColor
                axisLineColor = graphTextColor
                gridColor = graphTextColor
                granularity = 1f / 4
            }
            axisRight.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return "${(value * 4f).toInt()}"
                }
            }

            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                setDrawInside(false)
                textColor = graphTextColor

                // Добавляем кастомные метки
                setCustom(
                    listOf(
                        LegendEntry(
                            "+Вайб",
                            Legend.LegendForm.SQUARE,
                            10f,
                            2f,
                            null,
                            positiveGraphColor
                        ),
                        LegendEntry(
                            "-Вайб",
                            Legend.LegendForm.SQUARE,
                            10f,
                            2f,
                            null,
                            negativeGraphColor
                        ),
                        LegendEntry(
                            "Кол-во",
                            Legend.LegendForm.SQUARE,
                            10f,
                            2f,
                            null,
                            countGraphColor
                        )
                    )
                )
            }

            description.isEnabled = false
            setTouchEnabled(true)
            setFitBars(true)

            post {
                animateY(1000)
                invalidate()
            }
        }
    }
}