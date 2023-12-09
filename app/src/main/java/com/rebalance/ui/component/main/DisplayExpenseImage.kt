package com.rebalance.ui.component.main

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.rebalance.backend.service.BackendService
import com.rebalance.util.alertUser
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
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

    LaunchedEffect(showPicture) {
        if (showPicture) {
            image = backendService.getImageByExpenseId(expenseId)
            if (image != null) {
                icon = Bitmap.createScaledBitmap(
                    image!!.asAndroidBitmap(), 100, 100, false
                ).asImageBitmap()
            }
        }
    }

    if (icon != null) {
        if (showPicture && image != null) {
            AlertDialog(
                onDismissRequest = { onIconClick(false) },
                title = {
                    Box(
                        modifier = Modifier.clickable(onClick = {
                            onIconClick(false)
                        })
                    ) {
                        Image(
                            bitmap = image!!,
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
            onIconClick(true)
        }) {
            Image(
                bitmap = icon!!,
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
