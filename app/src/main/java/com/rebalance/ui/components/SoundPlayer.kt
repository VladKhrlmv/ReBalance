package com.rebalance.ui.components

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.annotation.RawRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SoundPlayer(@RawRes soundResId: Int, soundName: String, selectedSound: MutableState<Int>, context: Context, isSystemSound: Boolean = false) {
    var mediaPlayer: MediaPlayer? = null
    val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val playSound = {
        mediaPlayer?.release()
        if (isSystemSound) {
            mediaPlayer = MediaPlayer.create(context, ringtoneManager)
        } else {
            mediaPlayer = MediaPlayer.create(context, soundResId)
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
            selected = selectedSound.value == soundResId,
            onClick = {
                selectedSound.value = soundResId
                playSound()
            }
        )
        Text(
            text = soundName,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    selectedSound.value = soundResId
                    playSound()
                }
        )
    }
}
