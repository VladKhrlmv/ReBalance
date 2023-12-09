package com.rebalance.ui.component.main

import android.graphics.Canvas
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.rebalance.backend.dto.BarChartItem
import com.rebalance.ui.theme.crimsonColor
import com.rebalance.ui.theme.forestGreenColor
import java.text.DecimalFormat


@Composable
fun BarChart(
    data: List<BarChartItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val onBackground = MaterialTheme.colorScheme.onBackground.toArgb()
        Crossfade(
            targetState = data,
            modifier = Modifier
                .fillMaxWidth()
        ) { barChartData ->
            AndroidView(factory = { context ->
                HorizontalBarChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (130 * data.size),
                    )
                    this.description.isEnabled = false
                    this.legend.isEnabled = false
                    this.axisRight.isEnabled = false
                    this.axisLeft.isEnabled = false
                    this.xAxis.setDrawGridLines(false)
                    this.xAxis.setDrawAxisLine(false)
                    this.xAxis.setDrawGridLinesBehindData(false)
                    this.xAxis.setDrawLimitLinesBehindData(false)
                    this.xAxis.granularity = 1f
                    this.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    this.xAxis.textSize = 16f
                    this.xAxis.typeface = Typeface.DEFAULT
                    this.xAxis.labelCount = data.size
                    this.xAxis.textColor = onBackground
                    this.axisLeft.spaceBottom = 20f
                    this.axisRight.spaceTop = 10f
                    this.setDrawValueAboveBar(false)
                    this.setTouchEnabled(false)
                    this.setPinchZoom(false)
                    this.isDoubleTapToZoomEnabled = false
                    this.setNoDataText("No data")
                    this.setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
                    this.renderer = CustomBarChartRenderer(
                        this,
                        this.animator,
                        this.viewPortHandler
                    )
                }
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .testTag("groupBarChart"),
                update = {
//                    updateBarChartWithData(it, barChartData, onBackground)
                }
            )
        }
    }
}

//fun updateBarChartWithData(
//    chart: HorizontalBarChart,
//    data: List<BarChartData>,
//    textColor: Int
//) {
//    val entries = ArrayList<BarEntry>()
//    for (i in data.indices) {
//        val item = data[i]
//        entries.add(BarEntry(i.toFloat(), item.data.second.toFloat()))
//    }
//    val ds = BarDataSet(entries, "")
//    ds.colors = data.map { if (it.data.second >= 0) forestGreenColor.toArgb() else crimsonColor.toArgb() }
//    ds.valueTextColor = textColor
//    ds.valueFormatter = object : ValueFormatter() {
//        val format = DecimalFormat("###,###,###.##")
//        override fun getFormattedValue(value: Float): String {
//            return format.format(value)
//        }
//    }
//    ds.valueTextSize = 16f
//    ds.valueTypeface = Typeface.DEFAULT_BOLD
//    val d = BarData(ds)
//    chart.data = d
//    chart.xAxis.valueFormatter = object : ValueFormatter() {
//        override fun getFormattedValue(value: Float): String {
//            return data[value.toInt()].data.first
//        }
//    }
//    chart.invalidate()
//}

class CustomBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawValues(c: Canvas?) {
        // if values are drawn
        if (isDrawingValuesAllowed(mChart)) {
            val dataSets = mChart.barData.dataSets
            val valueOffsetPlus = Utils.convertDpToPixel(5f)
            var posOffset = 0f
            var negOffset = 0f
            val drawValueAboveBar = mChart.isDrawValueAboveBarEnabled
            for (i in 0 until mChart.barData.dataSetCount) {
                val dataSet = dataSets[i]
                if (!shouldDrawValues(dataSet)) continue
                val isInverted = mChart.isInverted(dataSet.axisDependency)

                // apply the text-styling defined by the DataSet
                applyValueTextStyle(dataSet)
                val halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f
                val formatter: IValueFormatter = dataSet.valueFormatter

                // get the buffer
                val buffer = mBarBuffers[i]
                val phaseY = mAnimator.phaseY
                val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
                iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
                iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)

                // if only single values are drawn (sum)
                if (!dataSet.isStacked) {
                    var j = 0
                    while (j < buffer.buffer.size * mAnimator.phaseX) {
                        val y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f
                        if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1])) break
                        if (!mViewPortHandler.isInBoundsX(buffer.buffer[j])) {
                            j += 4
                            continue
                        }
                        if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                            j += 4
                            continue
                        }
                        val entry = dataSet.getEntryForIndex(j / 4)
                        val `val` = entry.y
                        val formattedValue =
                            formatter.getFormattedValue(`val`, entry, i, mViewPortHandler)

                        // calculate the correct offset depending on the draw position of the value
                        val valueTextWidth =
                            Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                        posOffset =
                            if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                        negOffset =
                            ((if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus)
                                    - (buffer.buffer[j + 2] - buffer.buffer[j]))
                        if (isInverted) {
                            posOffset = -posOffset - valueTextWidth
                            negOffset = -negOffset - valueTextWidth
                        }
                        if (dataSet.isDrawValuesEnabled) {
                            drawValue(
                                c,
                                formattedValue,
                                buffer.buffer[j + 2] + if (`val` >= 0) posOffset else negOffset,
                                y + halfTextHeight,
                                dataSet.getValueTextColor(j / 2)
                            )
                        }
                        if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                            val icon = entry.icon
                            var px = buffer.buffer[j + 2] + if (`val` >= 0) posOffset else negOffset
                            var py = y
                            px += iconsOffset.x
                            py += iconsOffset.y
                            Utils.drawImage(
                                c,
                                icon, px.toInt(), py.toInt(),
                                icon.intrinsicWidth,
                                icon.intrinsicHeight
                            )
                        }
                        j += 4
                    }

                    // if each value of a potential stack should be drawn
                } else {
                    val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
                    var bufferIndex = 0
                    var index = 0
                    while (index < dataSet.entryCount * mAnimator.phaseX) {
                        val entry = dataSet.getEntryForIndex(index)
                        val color = dataSet.getValueTextColor(index)
                        val vals = entry.yVals

                        // we still draw stacked bars, but there is one
                        // non-stacked
                        // in between
                        if (vals == null) {
                            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[bufferIndex + 1])) break
                            if (!mViewPortHandler.isInBoundsX(buffer.buffer[bufferIndex])) continue
                            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[bufferIndex + 1])) continue
                            val `val` = entry.y
                            val formattedValue = formatter.getFormattedValue(
                                `val`,
                                entry, i, mViewPortHandler
                            )

                            // calculate the correct offset depending on the draw position of the value
                            val valueTextWidth =
                                Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                            posOffset =
                                if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                            negOffset =
                                if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus
                            if (isInverted) {
                                posOffset = -posOffset - valueTextWidth
                                negOffset = -negOffset - valueTextWidth
                            }
                            if (dataSet.isDrawValuesEnabled) {
                                drawValue(
                                    c, formattedValue, buffer.buffer[bufferIndex + 2]
                                            + if (entry.y >= 0) posOffset else negOffset,
                                    buffer.buffer[bufferIndex + 1] + halfTextHeight, color
                                )
                            }
                            if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                                val icon = entry.icon
                                var px = (buffer.buffer[bufferIndex + 2]
                                        + if (entry.y >= 0) posOffset else negOffset)
                                var py = buffer.buffer[bufferIndex + 1]
                                px += iconsOffset.x
                                py += iconsOffset.y
                                Utils.drawImage(
                                    c,
                                    icon, px.toInt(), py.toInt(),
                                    icon.intrinsicWidth,
                                    icon.intrinsicHeight
                                )
                            }
                        } else {
                            val transformed = FloatArray(vals.size * 2)
                            var posY = 0f
                            var negY = -entry.negativeSum
                            run {
                                var k = 0
                                var idx = 0
                                while (k < transformed.size) {
                                    val value = vals[idx]
                                    var y: Float
                                    if (value == 0.0f && (posY == 0.0f || negY == 0.0f)) {
                                        // Take care of the situation of a 0.0 value, which overlaps a non-zero bar
                                        y = value
                                    } else if (value >= 0.0f) {
                                        posY += value
                                        y = posY
                                    } else {
                                        y = negY
                                        negY -= value
                                    }
                                    transformed[k] = y * phaseY
                                    k += 2
                                    idx++
                                }
                            }
                            trans.pointValuesToPixel(transformed)
                            var k = 0
                            while (k < transformed.size) {
                                val `val` = vals[k / 2]
                                val formattedValue = formatter.getFormattedValue(
                                    `val`,
                                    entry, i, mViewPortHandler
                                )

                                // calculate the correct offset depending on the draw position of the value
                                val valueTextWidth =
                                    Utils.calcTextWidth(mValuePaint, formattedValue).toFloat()
                                posOffset =
                                    if (drawValueAboveBar) valueOffsetPlus else -(valueTextWidth + valueOffsetPlus)
                                negOffset =
                                    if (drawValueAboveBar) -(valueTextWidth + valueOffsetPlus) else valueOffsetPlus
                                if (isInverted) {
                                    posOffset = -posOffset - valueTextWidth
                                    negOffset = -negOffset - valueTextWidth
                                }
                                val drawBelow = `val` == 0.0f && negY == 0.0f && posY > 0.0f ||
                                        `val` < 0.0f
                                val x = (transformed[k]
                                        + if (drawBelow) negOffset else posOffset)
                                val y =
                                    (buffer.buffer[bufferIndex + 1] + buffer.buffer[bufferIndex + 3]) / 2f
                                if (!mViewPortHandler.isInBoundsTop(y)) break
                                if (!mViewPortHandler.isInBoundsX(x)) {
                                    k += 2
                                    continue
                                }
                                if (!mViewPortHandler.isInBoundsBottom(y)) {
                                    k += 2
                                    continue
                                }
                                if (dataSet.isDrawValuesEnabled) {
                                    drawValue(c, formattedValue, x, y + halfTextHeight, color)
                                }
                                if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                                    val icon = entry.icon
                                    Utils.drawImage(
                                        c,
                                        icon,
                                        (x + iconsOffset.x).toInt(),
                                        (y + iconsOffset.y).toInt(),
                                        icon.intrinsicWidth,
                                        icon.intrinsicHeight
                                    )
                                }
                                k += 2
                            }
                        }
                        bufferIndex =
                            if (vals == null) bufferIndex + 4 else bufferIndex + 4 * vals.size
                        index++
                    }
                }
                MPPointF.recycleInstance(iconsOffset)
            }
        }
    }
}
