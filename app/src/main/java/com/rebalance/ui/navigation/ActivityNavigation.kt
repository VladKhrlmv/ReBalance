package com.rebalance.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import kotlin.reflect.KClass

//TODO: do not allow going back
fun <T : ComponentActivity> switchActivityTo(context: Context, activity: KClass<T>) {
    context.startActivity(Intent(context, activity.java))
}
