package com.rebalance.ui.components

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.rebalance.R
import com.rebalance.backend.entities.Expense
import com.rebalance.backend.service.BackendService
import com.rebalance.backend.service.BarChartData
import com.rebalance.ui.theme.blackColor
import com.rebalance.ui.theme.greenColor
import com.rebalance.ui.theme.redColor
import java.text.DecimalFormat

@Composable
fun BarChart(
    data: List<BarChartData>
) {
    Column(
        modifier = Modifier
            .padding(18.dp)
            .size(320.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Crossfade(targetState = data) { barChartData ->
            AndroidView(factory = { context ->
                HorizontalBarChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.axisRight.isEnabled = false
                    this.axisLeft.isEnabled = false
                    //this.xAxis.isEnabled = false
                    this.xAxis.setDrawGridLines(false)
                    this.xAxis.setDrawAxisLine(false)
                    this.xAxis.setDrawGridLinesBehindData(false)
                    this.xAxis.setDrawLimitLinesBehindData(false)
                    this.xAxis.granularity = 1f
                    this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    this.xAxis.textSize = 16f
                    this.xAxis.typeface = Typeface.DEFAULT
                    this.setNoDataText("No data")
                    this.setNoDataTextColor(R.color.black)
                    this.setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
                }
            },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    updateBarChartWithData(it, barChartData)
                })
        }
    }

}

fun updateBarChartWithData(
    chart: HorizontalBarChart,
    data: List<BarChartData>
) {
    val entries = ArrayList<BarEntry>()
    for (i in data.indices) {
        val item = data[i]
        entries.add(BarEntry(i.toFloat(), item.value.toFloat() ?: 0F))
    }
    val ds = BarDataSet(entries, "")
    ds.colors = data.map { if (it.value >= 0) greenColor.toArgb() else redColor.toArgb() }
    ds.valueFormatter = object : ValueFormatter() {
        val format = DecimalFormat("###,###,###.##")
        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }
    ds.valueTextColor = blackColor.toArgb()
    ds.valueTextSize = 18f
    ds.valueTypeface = Typeface.DEFAULT_BOLD
    val d = BarData(ds)
    chart.data = d
    chart.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return data[value.toInt()].debtor
        }
    }
    chart.invalidate()
}