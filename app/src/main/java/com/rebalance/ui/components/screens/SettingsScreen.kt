package com.rebalance.ui.components.screens

import android.graphics.Typeface
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.NotificationService
import com.rebalance.Preferences
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
            Text(
                text = "Notification sound",
                modifier = Modifier
                    .padding(10.dp),
                fontFamily = FontFamily(Typeface.DEFAULT),
                fontSize = 32.sp,
            )
            val context = LocalContext.current

            var selectedChannel =
                remember { mutableStateOf(Preferences(context).read().currNotificationChannel) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally)
            ) {
                SoundPlayer(
                    0,
                    "Default System Sound",
                    selectedChannel,
                    context,
                    "systemChannel",
                    isSystemSound = true
                )
                SoundPlayer(R.raw.sound1, "Sound 1", selectedChannel, context, "channel1")
                SoundPlayer(R.raw.sound2, "Sound 2", selectedChannel, context, "channel2")
                SoundPlayer(R.raw.sound3, "Sound 3", selectedChannel, context, "channel3")
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        NotificationService(context).sendNotification("Test notification")
                    }
                ) {
                    Text("Send Test Notification")
                }
            }
        }
    }
}
