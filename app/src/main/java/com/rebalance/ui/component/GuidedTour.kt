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
import androidx.navigation.NavHostController
import com.rebalance.service.Preferences
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo

data class TourStep(
    val screen: Routes,
    val anchor: Offset,
    val text: String,
    val isEnd: Boolean = false
)

class GuidedTour(
    private val tourSteps: List<TourStep>,
    val context: Context,
    val navHostController: NavHostController
) {
    val preferences = Preferences(context).read()
    var isActive by mutableStateOf(preferences.firstLaunch)
    var currentStepIndex by mutableStateOf(0)
    val currentStep get() = if (isActive) tourSteps.getOrNull(currentStepIndex) else null

    fun nextStep() {
        val currentScreen = currentStep?.screen
        currentStepIndex++
        val nextScreen = currentStep?.screen

        if (currentScreen != nextScreen) {
            navigateSingleTo(navHostController, nextScreen ?: return)
        }
    }

    fun skipTour() {
        isActive = false
        preferences.firstLaunch = false
        Preferences(context).write(preferences)
    }
}

@Composable
fun ToolTipOverlay(context: Context, navHostController: NavHostController) {
    val tooltips = listOf(
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "Welcome to the ReBalance application"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "This is your personal screen"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "Here you can see your expenses grouped by categories and filter them by date"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "You can also see the overview for day, week, month and year"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "For the detailed view, click the list button above"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 1100f),
            "Here you can navigate to another screens"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 200f),
            "This is a group screen"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 600f),
            "Here you can create groups and share expenses with your friends"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 600f),
            "You would see the graph of balances, but you can also switch to the list view for more details"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 1100f),
            "You can add expenses by clicking the (+) button below"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 200f),
            "To add an expense, just provide some basic information about it"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 1100f),
            "You can also create it for a group, choose who would pay and attach a photo"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 200f),
            "To save the expense, click Save button"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "That sums up the quick introduction to the app. Use with a pleasure :)",
            isEnd = true
        ),
    )
    val guidedTour = remember {
        GuidedTour(
            tooltips,
            context,
            navHostController
        )
    }
    if (guidedTour.isActive) {
        guidedTour.currentStep?.let { currentStep ->
            if (currentStep.screen.route == navHostController.currentBackStackEntry?.destination?.route) {
                ToolTipStep(
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
                        Button(onClick = { guidedTour.nextStep() }) {
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
