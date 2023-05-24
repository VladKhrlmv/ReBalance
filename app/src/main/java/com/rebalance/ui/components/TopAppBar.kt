package com.rebalance.ui.components

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.Preferences
import com.rebalance.PreferencesData
import com.rebalance.R
import com.rebalance.SignInActivity
import com.rebalance.ui.components.screens.navigation.ScreenNavigationItem
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.LogOut
import compose.icons.evaicons.fill.PieChart
import compose.icons.evaicons.fill.Settings

@ExperimentalMaterial3Api
@Composable
fun TopAppBar(
    pieChartActive: Boolean,
    onPieChartActiveChange: () -> Unit,
    logout: Boolean,
    navController: NavHostController
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

                IconButton(onClick = { navController.navigate(ScreenNavigationItem.Settings.route) }) {
                    Icon(EvaIcons.Fill.Settings, "Settings")
                }

                IconButton(onClick = {
                    Preferences(context).write(
                        PreferencesData(
                            "",
                            "-1",
                            -1,
                            false,
                            "systemChannel"
                        )
                    )
                    context.startActivity(Intent(context, SignInActivity::class.java))
                }, modifier = Modifier.testTag("logout")) {
                    Icon(EvaIcons.Fill.LogOut, "Logout")
                }
            }
        }
    )
}
