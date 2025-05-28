package com.example.moodnote.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmotionCalendarAdapter(
    private val context: Context,
    private var data: List<String>
) : BaseAdapter() {

    fun setData(data: List<String>){
        CoroutineScope(Dispatchers.Main).launch {
            this@EmotionCalendarAdapter.data = data
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): String = data[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView: TextView = convertView as? TextView ?: TextView(context).apply {

            val padding = (8 * context.resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            gravity = android.view.Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setTextColor(android.graphics.Color.BLACK)
            textSize = 16f
        }
        textView.text = getItem(position)
        return textView
    }
}
