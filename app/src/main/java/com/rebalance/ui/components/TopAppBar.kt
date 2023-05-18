package com.rebalance.ui.components

import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.rebalance.*
import com.rebalance.R
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.LogOut
import compose.icons.evaicons.fill.PieChart

@ExperimentalMaterial3Api
@Composable
fun TopAppBar(
    pieChartActive: Boolean,
    onPieChartActiveChange: () -> Unit,
    logout: Boolean
) {
    val context = LocalContext.current
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
        actions = {
            if (logout) {
                IconButton(
                    onClick = onPieChartActiveChange,
                    modifier = Modifier.testTag("viewSwitcher")
                ) {
                    Icon(
                        if (pieChartActive) Icons.Filled.List else EvaIcons.Fill.PieChart,
                        "Pie chart or list"
                    )

                }

                IconButton(onClick = {
                    Preferences(context).write(PreferencesData("", "-1", -1))
                    context.startActivity(Intent(context, SignInActivity::class.java))
                }, modifier = Modifier.testTag("logout")) {
                    Icon(EvaIcons.Fill.LogOut, "Logout")
                }
            }
        }
    )
}
