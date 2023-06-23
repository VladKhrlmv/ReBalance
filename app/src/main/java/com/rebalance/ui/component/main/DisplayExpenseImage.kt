package com.rebalance.ui.component.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService
import com.rebalance.utils.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Image

@Composable
fun DisplayExpenseImage(
    preferences: PreferencesData,
    globalId: Long?,
    showPicture: MutableState<Boolean>,
    context: Context
) {
    val imgBase64 = BackendService(preferences).getExpensePicture(globalId)
    if (imgBase64 != null) {
        if (showPicture.value) {
            AlertDialog(
                onDismissRequest = { showPicture.value = false },
                title = {
                    Box(
                        modifier = Modifier.clickable(onClick = {
                            showPicture.value = false
                        })
                    ) {
                        Image(
                            bitmap = BitmapFactory.decodeByteArray(
                                imgBase64,
                                0,
                                imgBase64.size
                            ).asImageBitmap(),
                            contentDescription = "Image",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                text = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(text = "Click again to close")
                    }
                },
                tonalElevation = 20.dp,
                confirmButton = {}
            )
        }
        IconButton(onClick = {
            showPicture.value = true
        }) {
            Image(
                bitmap = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeByteArray(
                        imgBase64,
                        0,
                        imgBase64.size
                    ), 100, 100, false
                ).asImageBitmap(),
                contentDescription = "Expanse image as an icon"
            )
        }
    } else {
        IconButton(onClick = {
            alertUser(
                "No image for this expense",
                context
            )
        }) {
            Icon(EvaIcons.Fill.Image, "Image placeholder")
        }
    }
}
