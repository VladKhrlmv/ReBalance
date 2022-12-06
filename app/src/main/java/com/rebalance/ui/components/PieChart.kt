package com.rebalance.ui.components

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.rebalance.R
import java.util.*
import com.rebalance.ui.theme.*

data class PieChartData (var category: String, var value: Float)

val getPieChartData = listOf(
    PieChartData("Medicine", 34.68F),
    PieChartData("Clothing", 16.60F),
    PieChartData("Food", 16.15F),
    PieChartData("Activities", 15.62F),
)

@Composable
fun PieChart() {
    Column(
        modifier = Modifier
            .padding(18.dp)
            .size(320.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Crossfade(targetState = getPieChartData) { pieChartData ->
            AndroidView(factory = { context ->
                PieChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                    this.description.isEnabled = false
                    this.isDrawHoleEnabled = true
                    this.legend.isEnabled = false
                    this.setEntryLabelColor(R.color.white)
                    this.transparentCircleRadius = 0F
                }
            },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    updatePieChartWithData(it, pieChartData)
                })
        }
    }
}

fun updatePieChartWithData(
    chart: PieChart,
    data: List<PieChartData>
) {
    val entries = ArrayList<PieEntry>()

    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value ?: 0.toFloat(), item.category ?: ""))
    }

    val ds = PieDataSet(entries, "")

    ds.colors = arrayListOf(
        greenColor.toArgb(),
        blueColor.toArgb(),
        redColor.toArgb(),
        yellowColor.toArgb(),
    )
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.sliceSpace = 2f
    ds.valueTextColor = R.color.white
    ds.valueTextSize = 18f
    ds.valueTypeface = Typeface.DEFAULT_BOLD
    val d = PieData(ds)
    chart.data = d
    chart.invalidate()
}