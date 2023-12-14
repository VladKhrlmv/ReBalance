package com.rebalance.ui.component.main.scaffold

import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.R
import com.rebalance.activity.AuthenticationActivity
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.LogOut
import compose.icons.evaicons.fill.Settings
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun TopAppBar(
    logout: Boolean,
    navHostController: NavHostController,
    backButton: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val backendService = remember { BackendService.get() }
    val topAppBarScope = rememberCoroutineScope()

    var logoutResult by remember { mutableStateOf(false) }


    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
        navigationIcon = {
            backButton()
        },
        actions = {
            if (logout) {
                content()
                IconButton(onClick = {
                    navigateSingleTo(navHostController, Routes.Settings)
                }) {
                    Icon(EvaIcons.Fill.Settings, "Settings")
                }

                IconButton(onClick = {
                    topAppBarScope.launch {
                        logoutResult = backendService.logout(true)
                    }
                }, modifier = Modifier.testTag("logout")) {
                    Icon(EvaIcons.Fill.LogOut, "Logout")
                }
                if (logoutResult) {
                    context.startActivity(Intent(context, AuthenticationActivity::class.java))
                }
            }
        }
    )
}
