package com.rebalance.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rebalance.backend.service.BackendService
import com.rebalance.ui.navigation.Routes
import com.rebalance.ui.navigation.navigateSingleTo
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.*
import kotlinx.coroutines.launch

data class TourStep(
    val screen: Routes,
    val anchor: Offset,
    val text: String,
    val isEnd: Boolean = false,
    val icon: ImageVector? = null,
    val iconContent: String? = null
)

@Composable
fun ToolTipOverlay(navHostController: NavHostController) {
    Log.d("tooltip", "init")
    val backendService = remember { BackendService.get() }
    val tourScope = rememberCoroutineScope()
    var isActive by rememberSaveable { mutableStateOf(backendService.isFirstLaunch()) }

    val tourSteps = listOf(
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
            "Here you can see your expenses grouped by categories in pie chart",
            icon = EvaIcons.Fill.PieChart,
            iconContent = "pie chart"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "You can also see the overview for day, week, month and year"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "For the detailed view, click the list button above",
            icon = Icons.Filled.List,
            iconContent = "list"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "By clicking on pie chart slice, you will be redirected to the list of expenses from this category"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 1000f),
            "Here you can navigate to another screens"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 200f),
            "This is a group screen"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 200f),
            "Here you can create groups and share expenses with your friends"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 200f),
            "You would see the graph of balances, but you can also switch to the list view for more details"
        ),
        TourStep(
            Routes.Group,
            Offset(100f, 1000f),
            "You can add expenses by clicking the button below",
            icon = EvaIcons.Fill.Plus,
            iconContent = "plus"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 200f),
            "To add an expense, just provide some basic information about it"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 200f),
            "You can also create it for a group, choose who would pay and attach a photo",
            icon = EvaIcons.Fill.Image,
            iconContent = "image"
        ),
        TourStep(
            Routes.AddSpending,
            Offset(100f, 1000f),
            "To save the expense, click the button below",
            icon = EvaIcons.Fill.Save,
            iconContent = "save"
        ),
        TourStep(
            Routes.Personal,
            Offset(100f, 200f),
            "That sums up the quick introduction to the app. Use with a pleasure",
            icon = EvaIcons.Fill.SmilingFace,
            iconContent = "smile",
            isEnd = true
        ),
    )
    var currentStepIndex by rememberSaveable { mutableStateOf(0) }

    if (isActive) {
        tourSteps.getOrNull(currentStepIndex)?.let { step ->
            if (step.screen.route != navHostController.currentBackStackEntry?.destination?.route) {
                Log.d("tooltip", "next screen")
                navigateSingleTo(navHostController, step.screen)
            }
            ToolTipStep(
                anchor = step.anchor,
                text = step.text,
                icon = currentStep.icon,
                iconContentDescription = currentStep.iconContent,
                isEnd = step.isEnd,
                nextStep = {
                    currentStepIndex += 1
                    Log.d("tooltip", "next tip $currentStepIndex")
                },
                skipTour = {
                    tourScope.launch {
                        Log.d("tooltip", "update first launch")
                        isActive = backendService.updateFirstLaunch(false)
                    }
                }
            )

        }
    }
}

@Composable
fun ToolTipStep(
    anchor: Offset,
    text: String,
    icon: ImageVector?,
    iconContentDescription: String?,
    isEnd: Boolean,
    nextStep: () -> Unit,
    skipTour: () -> Unit
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
                .background(MaterialTheme.colorScheme.primaryContainer, shape)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(280.dp)
            ) {
                TextWithInlineIcon(
                    text = text,
                    icon = icon,
                    iconContentDescription = iconContentDescription,
                    textSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isEnd) {
                        Text(
                            text = "Skip",
                            modifier = Modifier.clickable(onClick = { skipTour() }),
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { nextStep() }) {
                            Text("Next")
                        }
                    } else {
                        Button(onClick = { skipTour() }) {
                            Text("End")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TextWithInlineIcon(text: String, icon: ImageVector?, iconContentDescription: String?, textSize: TextUnit) {
    val inlineContentId = "inlineIcon"

    val annotatedText = buildAnnotatedString {
        append(text)

        icon?.let {
            append(" ")
            appendInlineContent(inlineContentId, iconContentDescription ?: "[icon]")
        }
    }

    val inlineContent = mapOf(
        Pair(
            inlineContentId,
            InlineTextContent(
                placeholder = Placeholder(
                    width = textSize,
                    height = textSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Icon(
                    imageVector = icon!!,
                    contentDescription = iconContentDescription,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        )
    )

    Text(text = annotatedText, inlineContent = inlineContent, fontSize = textSize)
}