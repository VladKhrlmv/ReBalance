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

fun currencyRegex(): Regex {
    return """[a-zA-Z]{0,3}""".toRegex()
}

fun costValueRegex(): Regex {
    return """^\d{0,12}[.,]?\d{0,2}${'$'}""".toRegex()
}

fun positiveIntegerRegex(): Regex {
    return """\d{1,9}""".toRegex()
}