package com.rebalance.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import kotlin.reflect.KClass

//TODO: do not allow going back
fun <T : ComponentActivity> switchActivityTo(context: Context, activity: KClass<T>) {
    val intent = Intent(context, activity.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}
