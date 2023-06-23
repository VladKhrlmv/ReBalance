package com.rebalance.utils

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat

fun alertUser(message: String, context: Context) {
    ContextCompat.getMainExecutor(context).execute {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
