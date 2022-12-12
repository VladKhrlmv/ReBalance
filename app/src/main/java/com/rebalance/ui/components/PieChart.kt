package com.rebalance.ui.components

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

data class PieChartData (var category: String, var value: Double)

val getPieChartData = listOf(
    PieChartData("Medicine", 34.68),
    PieChartData("Clothing", 16.60),
    PieChartData("Food", 16.15),
    PieChartData("Activities", 15.62),
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
                    this.centerText = String.format("%.2f", pieChartData.sumOf { it.value })
                    this.setCenterTextSize(26f)
                    this.setCenterTextColor(redColor.toArgb())
                    this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
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
        entries.add(PieEntry(item.value.toFloat() ?: 0F, item.category ?: ""))
    }

    val ds = PieDataSet(entries, "")

    ds.colors = data.map { categoryColor[it.category]?.toArgb() ?: greyColor.toArgb() }
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.sliceSpace = 2F
    ds.setValueTextColors(data.map {
        categoryColor[it.category]?.red?.let { red ->
            categoryColor[it.category]?.blue?.let { blue ->
                categoryColor[it.category]?.green?.let { green ->
                    categoryColor[it.category]?.alpha?.let { alpha ->
                        Color(
                            red =  1f - red,
                            green = 1f - green,
                            blue = 1f - blue,
                            alpha = alpha
                        ).toArgb()
                    }
                }
            }
        }
    })
    ds.valueTextSize = 18f
    ds.valueTypeface = Typeface.DEFAULT_BOLD
    val d = PieData(ds)
    chart.data = d
    chart.invalidate()
}