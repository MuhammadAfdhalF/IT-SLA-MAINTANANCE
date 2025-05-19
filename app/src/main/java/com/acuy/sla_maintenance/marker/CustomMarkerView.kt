package com.acuy.sla_maintenance.marker

import android.content.Context
import android.widget.TextView
import com.acuy.sla_maintenance.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById(R.id.tvContent)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e != null) {
            val hours = e.y.toInt()
            val minutes = ((e.y - hours) * 60).toInt()
            tvContent.text = String.format("%d jam %02d menit", hours, minutes)
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
