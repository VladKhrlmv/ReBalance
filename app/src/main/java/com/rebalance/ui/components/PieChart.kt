package com.rebalance.ui.components

import android.graphics.Typeface
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.rebalance.R
import com.rebalance.backend.GlobalVars
import com.rebalance.backend.api.jsonArrayToExpenses
import com.rebalance.backend.api.sendGet
import com.rebalance.backend.entities.Expense
import com.rebalance.ui.theme.categoryColor
import com.rebalance.ui.theme.greyColor
import com.rebalance.ui.theme.redColor
import java.text.DecimalFormat


data class PieChartData(var category: String, var value: Double)

@RequiresApi(Build.VERSION_CODES.N)
fun getPieChartData(): ArrayList<PieChartData> {
    val policy = ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    var entries = ArrayList<PieChartData>()

    var jsonBodyGet = sendGet(
        "http://${GlobalVars().getIp()}/groups/2/expenses"
    )
    var listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
    println(listExpense)
    var categoryMap: HashMap<String, Int> = HashMap()
    listExpense.forEach { entry ->
        if (categoryMap.containsKey(entry.getCategory())) {
            categoryMap[entry.getCategory()] =
                categoryMap.getValue(entry.getCategory()) + entry.getAmount()
        } else {
            categoryMap[entry.getCategory()] = entry.getAmount()
        }
    }
    for (entry in categoryMap.entries.iterator()) {
        entries.add(PieChartData(entry.key, entry.value.toDouble() / 100))
    }
    //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
    return entries
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun PieChart() {
    Column(
        modifier = Modifier
            .padding(18.dp)
            .size(320.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Crossfade(targetState = getPieChartData()) { pieChartData ->
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

@RequiresApi(Build.VERSION_CODES.N)
fun updatePieChartWithData(
    chart: PieChart,
    data: List<PieChartData>
) {
    val entries = ArrayList<PieEntry>()

    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value.toFloat(), item.category))
    }

    val ds = PieDataSet(entries, "")
    ds.colors = categoryColor.map { it.toArgb() }
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.valueFormatter = object : ValueFormatter() {
        val format = DecimalFormat("###,###,###.##")
        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
    }
    ds.sliceSpace = 2F
    ds.setValueTextColors(categoryColor.map {
        it.red.let { red ->
            it.blue.let { blue ->
                it.green.let { green ->
                    it.alpha.let { alpha ->
                        Color(
                            red = 1f - red,
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
    chart.centerText = String.format("%.2f", data.sumOf { it.value })
    chart.invalidate()
}
