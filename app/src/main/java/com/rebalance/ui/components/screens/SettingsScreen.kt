package com.rebalance.ui.components.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Your logic for playing and choosing a favorite sound goes here

        Button(onClick = { navController.popBackStack() }) {
            Text("Exit Settings")
        }
    }
}
