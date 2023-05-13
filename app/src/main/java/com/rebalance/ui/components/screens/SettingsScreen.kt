package com.rebalance.ui.components.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.R
import com.rebalance.ui.components.SoundPlayer

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            val context = LocalContext.current
            var selectedSound = remember { mutableStateOf(0) }

            Column(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)) {
                SoundPlayer(0, "Default System Sound", selectedSound, context, isSystemSound = true)
                SoundPlayer(R.raw.sound1, "Sound 1", selectedSound, context)
                SoundPlayer(R.raw.sound2, "Sound 2", selectedSound, context)
                SoundPlayer(R.raw.sound3, "Sound 3", selectedSound, context)

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { navController.popBackStack() }) {
                    Text("Exit Settings")
                }
            }
        }
    }
}
