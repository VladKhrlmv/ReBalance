package com.rebalance.ui.component.main.scaffold

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.rebalance.backend.service.ExpenseItem
import com.rebalance.ui.theme.categoryColors
import com.rebalance.ui.theme.darkBlueColor
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@Composable
fun PieChart(
    data: List<ExpenseItem>,
    pieChartActive: MutableState<Boolean>,
    openCategory: MutableState<String>,
    expandableListState: LazyListState
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .padding(18.dp)
            .size(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val onBackground = MaterialTheme.colorScheme.onBackground.toArgb()
        Crossfade(targetState = data) { pieChartData ->
            AndroidView(factory = { context ->
                PieChart(context).apply {
                    this.layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.setEntryLabelColor(darkBlueColor.toArgb())
                    this.isRotationEnabled = true
                    this.isDrawHoleEnabled = true
                    this.setHoleColor(Color.TRANSPARENT)
                    this.transparentCircleRadius = 0f
                    this.setCenterTextSize(20f)
                    this.holeRadius = 50f
                    this.setCenterTextColor(onBackground)
                    this.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                    this.setOnChartValueSelectedListener(object: OnChartValueSelectedListener{
                        override fun onValueSelected(e: Entry, h: Highlight) {
                            if (e is PieEntry) {
                                pieChartActive.value = !pieChartActive.value
                                openCategory.value = (e.data as ExpenseItem).text
                                val indexOfItem = data.indexOfFirst {
                                    it.text == openCategory.value
                                }
                                coroutineScope.launch {
                                    expandableListState.scrollToItem(index = indexOfItem, scrollOffset = 3)
                                }
                            }
                        }

                        override fun onNothingSelected() {}
                    })
                }
            },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    updatePieChartWithData(it, pieChartData)
                }
            )
        }
    }
}

fun updatePieChartWithData(
    chart: PieChart,
    data: List<ExpenseItem>
) {
    val entries = ArrayList<PieEntry>()

    val sum = data.sumOf { it.amount }
    val overlapLimit = 5f

    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(
            (item.amount / sum * 100).toFloat(),
            if (item.amount / sum * 100 >= overlapLimit)
                item.text
            else
                "",
            item
        ))
    }

    val ds = PieDataSet(entries, "")
    ds.colors = categoryColors.map { it.toArgb() }
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.valueFormatter = object : ValueFormatter() {
        val format = DecimalFormat("###,###,###.##")
        override fun getFormattedValue(value: Float): String {
            return format.format(value)
        }
        override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
            return if (value < overlapLimit)
                ""
            else
                "${getFormattedValue(value)}%"
        }
    }
    ds.sliceSpace = 2F
    ds.setValueTextColors(categoryColors.map { color ->
        fun isColorLight(color: Int): Boolean {
            val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
            return darkness < 0.5
        }

        val hsv = FloatArray(3)
        Color.colorToHSV(color.toArgb(), hsv)
        hsv[0] = (hsv[0] + 180) % 360

        if (isColorLight(color.toArgb())) {
            hsv[2] *= 0.4f
            hsv[1] = hsv[1].coerceAtLeast(0.5f)
        } else {
            hsv[2] = (hsv[2] + 0.5f).coerceAtMost(1f)
            hsv[1] = (hsv[1] + 0.5f).coerceAtMost(1f)
        }

        val textColor = Color.HSVToColor(hsv)
        textColor
    })
    ds.valueTextSize = 18f
    ds.valueTypeface = Typeface.DEFAULT_BOLD
    val d = PieData(ds)
    chart.data = d
    chart.centerText = String.format(if (sum >= 100000) "%,.0f" else "%,.2f", sum)
    chart.invalidate()
}

