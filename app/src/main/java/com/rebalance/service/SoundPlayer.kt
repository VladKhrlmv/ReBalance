package com.rebalance.service

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SoundPlayer(
    @RawRes soundResId: Int,
    soundName: String,
    selectedSound: String,
    context: Context,
    correspondingNotificationChannel: String,
    isSystemSound: Boolean = false,
    changeSelectedSound: (String) -> Unit
) {
    var mediaPlayer: MediaPlayer? = null
    val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val playSound = {
        mediaPlayer?.release()
        mediaPlayer = if (isSystemSound) {
            MediaPlayer.create(context, ringtoneManager)
        } else {
            MediaPlayer.create(context, soundResId)
        }
        mediaPlayer?.start()
    }

    val stopSound = {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    DisposableEffect(soundResId) {
        onDispose {
            stopSound()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedSound == correspondingNotificationChannel,
            onClick = {
                changeSelectedSound(correspondingNotificationChannel)
                playSound()
            }
        )
        Text(
            text = soundName,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    changeSelectedSound(correspondingNotificationChannel)
                    playSound()
                }
        )
    }
}
