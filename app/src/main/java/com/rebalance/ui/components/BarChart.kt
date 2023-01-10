package com.rebalance.ui.components

import android.graphics.Typeface
import android.os.Build
import android.os.StrictMode
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
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
import com.rebalance.backend.GlobalVars
import com.rebalance.backend.api.jsonArrayToApplicationUsers
import com.rebalance.backend.api.jsonArrayToExpenses
import com.rebalance.backend.api.sendGet
import com.rebalance.backend.entities.Expense
import com.rebalance.ui.theme.blackColor
import com.rebalance.ui.theme.greenColor
import com.rebalance.ui.theme.redColor
import java.text.DecimalFormat

data class BarChartData(var debtor: String, var value: Double)

@RequiresApi(Build.VERSION_CODES.N)
fun getBarChartData(): ArrayList<BarChartData> {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    var entries = ArrayList<BarChartData>()

    var jsonBodyGetUsersFromGroup = sendGet(
        "http://${GlobalVars().getIp()}/groups/1/users"
    )
    var userExpenseMap: HashMap<String, Int> = HashMap()

    var userList = jsonArrayToApplicationUsers(jsonBodyGetUsersFromGroup)
    println(userList)
    for(user in userList){
        var jsonBodyGet = sendGet(
            "http://${GlobalVars().getIp()}/groups/1/users/${user.getId()}/expenses"
        )
        var listExpense: List<Expense> = jsonArrayToExpenses(jsonBodyGet)
        var sumForUser: Int = 0
        for(expense in listExpense){
            sumForUser += expense.getAmount()
        }
        userExpenseMap[user.getUsername()] = sumForUser
    }
    for (entry in userExpenseMap.entries.iterator()) {
        entries.add(BarChartData(entry.key, entry.value.toDouble() / 100))
    }
    //todo https://stackoverflow.com/questions/6343166/how-can-i-fix-android-os-networkonmainthreadexception#:~:text=Implementation%20summary
    return entries
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun BarChart() {
    Column(
        modifier = Modifier
            .padding(18.dp)
            .size(320.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Crossfade(targetState = getBarChartData()) { barChartData ->
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