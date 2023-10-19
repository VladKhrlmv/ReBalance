package com.rebalance.ui.component.main

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Calendar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(mDate: MutableState<String>, modifier: Modifier) {
    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()

    val mYear: Int = mCalendar.get(Calendar.YEAR)
    val mMonth: Int = mCalendar.get(Calendar.MONTH)
    val mDay: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            mDate.value = "${String.format("%04d", year)}-${String.format("%02d", month + 1)}-${
                String.format(
                    "%02d",
                    dayOfMonth
                )
            }"
        }, mYear, mMonth, mDay
    )
    Column(modifier = modifier) {
        Box {
            TextField(
                value = if (mDate.value == "")
                    "${String.format("%04d", mYear)}-${
                        String.format(
                            "%02d",
                            mMonth + 1
                        )
                    }-${String.format("%02d", mDay)}"
                else mDate.value,
                onValueChange = { },
                enabled = true,
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                ),
                trailingIcon = {
                    Icon(EvaIcons.Fill.Calendar, "Date")
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
