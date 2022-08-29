package com.example.projectskelton.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.widget.TextView
import com.example.projectskelton.R
import com.example.projectskelton.data.repository.Run
import com.example.projectskelton.domain.util.TimeUtility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }


    @SuppressLint("NewApi")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        val currentRunId = e.x.toInt()
        val run = runs.get(currentRunId)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timeStamp
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val tvDate: TextView = findViewById<TextView>(R.id.tvDate) as TextView
        val tvAvgSpeed: TextView = findViewById<TextView>(R.id.tvAvgSpeed) as TextView
        val tvDistance: TextView = findViewById<TextView>(R.id.tvDistance) as TextView
        val tvTime: TextView = findViewById<TextView>(R.id.tvDuration) as TextView
        val tvCalories: TextView = findViewById<TextView>(R.id.tvCaloriesBurned) as TextView

        tvDate?.let {
            it.text = dateFormat.format(calendar.time)
        }
        tvAvgSpeed?.let {
            it.text = "${run.avgSpeedInKMH}Km/h"
        }
        tvDistance?.let {
            it.text = "${run.distanceInMetres / 1000}km"
        }
        tvTime?.let {
            it.text = TimeUtility.getFormattedTime(run.timeInMillis)
        }
        tvCalories?.let {
            it.text = "${run.caloriesBurnt}kcal"
        }

    }
}