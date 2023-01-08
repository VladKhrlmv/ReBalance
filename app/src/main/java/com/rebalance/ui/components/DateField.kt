package com.rebalance.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import android.widget.DatePicker
import java.util.Calendar
import java.util.Date

@Composable
fun DatePickerField(modifier: Modifier) {
    val mContext = LocalContext.current

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDate = remember { mutableStateOf("") }

    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            mDate.value = "$dayOfMonth/${month+1}/$year"
        }, mYear, mMonth, mDay
    )
    Column(modifier = modifier) {
        Box {
            TextField(
                value = mDate.value,
                onValueChange = {  },
                enabled = true,
                readOnly = true,
                label = {
                    Text(text = "Date")
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0f)
                    .clickable { mDatePickerDialog.show() }
            )
        }
    }
}