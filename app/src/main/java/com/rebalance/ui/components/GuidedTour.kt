package com.rebalance.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rebalance.Preferences

data class TourStep(val screen: String, val anchor: Offset, val text: String, val isEnd: Boolean = false)

class GuidedTour(private val tourSteps: List<TourStep>, context: Context) {
    val preferences = Preferences(context).read()
    var isActive by mutableStateOf(preferences.firstLaunch)
    var currentStepIndex by mutableStateOf(0)
    val context = context
    val currentStep: TourStep? get() = if (isActive) tourSteps.getOrNull(currentStepIndex) else null

    fun nextStep(navController: NavController) {
        val currentScreen = currentStep?.screen
        currentStepIndex++
        val nextScreen = currentStep?.screen

        if (currentScreen != nextScreen) {
            navController.navigate(nextScreen ?: return)
        }
    }

    fun skipTour() {
        isActive = false
        preferences.firstLaunch = false
        Preferences(context).write(preferences)
    }
}

@Composable
fun ToolTipOverlay(context: Context, navController: NavController) {
    val guidedTour = remember {
        GuidedTour(
            listOf(
                TourStep("personal", Offset(100f, 100f), "This is the personal screen.", isEnd = false),
                TourStep("add_spending", Offset(400f, 400f), "This is another tooltip.", isEnd = false),
                TourStep("group", Offset(400f, 400f), "This is the end tooltip.", isEnd = true),
            ),
            context
        )
    }
    if (guidedTour.isActive) {
        guidedTour.currentStep?.let { currentStep ->
            if (currentStep.screen == navController.currentBackStackEntry?.destination?.route) {
                ToolTipStep(
                    navController = navController,
                    anchor = currentStep.anchor,
                    text = currentStep.text,
                    isEnd = currentStep.isEnd,
                    guidedTour = guidedTour,
                )
            }
        }
    }
}

@Composable
fun ToolTipStep(
    navController: NavController,
    anchor: Offset,
    text: String,
    isEnd: Boolean,
    guidedTour: GuidedTour
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(anchor.x.toInt(), anchor.y.toInt()) }
                .shadow(4.dp, shape)
                .background(Color.White, shape)
                .padding(16.dp)
        ) {
            Column {
                Text(text = text)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isEnd) {
                        Button(onClick = { guidedTour.nextStep(navController) }) {
                            Text("Next")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Skip",
                            modifier = Modifier.clickable(onClick = guidedTour::skipTour),
                            color = Color.Red
                        )
                    } else {
                        Button(onClick = { guidedTour.skipTour() }) {
                            Text("End")
                        }
                    }
                }
            }
        }
    }
}
