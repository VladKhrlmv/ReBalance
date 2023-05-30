package com.rebalance.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rebalance.Preferences

data class TourStep(
    val screen: String,
    val anchor: Offset,
    val text: String,
    val isEnd: Boolean = false
)

class GuidedTour(private val tourSteps: List<TourStep>, val context: Context) {
    val preferences = Preferences(context).read()
    var isActive by mutableStateOf(preferences.firstLaunch)
    var currentStepIndex by mutableStateOf(0)
    val currentStep get() = if (isActive) tourSteps.getOrNull(currentStepIndex) else null

    fun nextStep(navController: NavController) {
        val currentScreen = currentStep?.screen
        currentStepIndex++
        val nextScreen = currentStep?.screen

        if (currentScreen != nextScreen) {
            navController.navigate(nextScreen ?: return) {
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
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
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "Welcome to the ReBalance application",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "This is your personal screen",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "Here you can see your expenses grouped by categories and filter them by date",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "You can also see the overview for day, week, month and year",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "For the detailed view, click the list button above",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 1100f),
                    "Here you can navigate to another screens",
                    isEnd = false
                ),
                TourStep("group", Offset(100f, 200f), "This is a group screen", isEnd = false),
                TourStep(
                    "group",
                    Offset(100f, 600f),
                    "Here you can create groups and share expenses with your friends",
                    isEnd = false
                ),
                TourStep(
                    "group",
                    Offset(100f, 600f),
                    "You would see the graph of balances, but you can also switch to the list view for more details",
                    isEnd = false
                ),
                TourStep(
                    "group",
                    Offset(100f, 1100f),
                    "You can add expenses by clicking the (+) button below",
                    isEnd = false
                ),
                TourStep(
                    "add_spending",
                    Offset(100f, 200f),
                    "To add an expense, just provide some basic information about it",
                    isEnd = false
                ),
                TourStep(
                    "add_spending",
                    Offset(100f, 1100f),
                    "You can also create it for a group, choose who would pay and attach a photo",
                    isEnd = false
                ),
                TourStep(
                    "add_spending",
                    Offset(100f, 200f),
                    "To save the expense, click Save button",
                    isEnd = false
                ),
                TourStep(
                    "personal",
                    Offset(100f, 200f),
                    "That sums up the quick introduction to the app. Use with a pleasure :)",
                    isEnd = true
                ),
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
            Column(
                modifier = Modifier
                    .width(280.dp)
            ) {
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
