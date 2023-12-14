package com.rebalance.ui.component.main

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.Close
import compose.icons.evaicons.fill.Image

@Composable
fun DisplayExpenseImage(
    expenseId: Long,
    context: Context,
    showPicture: Boolean,
    onIconClick: (Boolean) -> Unit
) {
    val backendService = remember { BackendService.get() }

    //TODO: save Image and show original name
    var icon by remember { mutableStateOf<ImageBitmap?>(null) }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(Unit) {
        image = backendService.getImageByExpenseId(expenseId)
        if (image != null) {
            icon = Bitmap.createScaledBitmap(
                image!!.asAndroidBitmap(), 100, 100, false
            ).asImageBitmap()
        }
    }

    if (icon != null) {
        if (showPicture && image != null) {
            Dialog(
                onDismissRequest = {
                    onIconClick(false)
                }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 50.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            bitmap = image!!,
                            contentDescription = "Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            IconButton(
                                onClick = { onIconClick(false) },
                            ) {
                                Icon(
                                    EvaIcons.Fill.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
        IconButton(
            onClick = {
                onIconClick(true)
            }
        ) {
            Image(
                bitmap = icon!!,
                contentDescription = "Expanse image as an icon"
            )
        }
    } else {
        IconButton(
            onClick = {
                alertUser(
                    "No image for this expense",
                    context
                )
            }
        ) {
            Icon(EvaIcons.Fill.Image, "Image placeholder")
        }
    }
}
