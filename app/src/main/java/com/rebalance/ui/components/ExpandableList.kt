package com.rebalance.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ExpandableCardModel(val id: Int, val title: String)

class CardsViewModel : ViewModel() {
    private val _cards = MutableStateFlow(listOf<ExpandableCardModel>())
    val cards: StateFlow<List<ExpandableCardModel>> get() = _cards

    private val _expandedCardIdsList = MutableStateFlow(listOf<Int>())
    val expandedCardIdsList: StateFlow<List<Int>> get() = _expandedCardIdsList

    init {
        getFakeData()
    }

    private fun getFakeData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val testList = arrayListOf<ExpandableCardModel>()
                repeat(20) { testList += ExpandableCardModel(id = it, title = "Card $it") }
                _cards.emit(testList)
            }
        }
    }

    fun onCardArrowClicked(cardId: Int) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }
}

@Composable
fun CardsScreen(viewModel: CardsViewModel) {
    val cards by viewModel.cards.collectAsState()
    val expandedCardIds by viewModel.expandedCardIdsList.collectAsState()

    Scaffold {
        LazyColumn {
            this.items(cards, ExpandableCardModel::id) { card ->
                ExpandableCard(
                    card = card,
                    onCardArrowClick = { viewModel.onCardArrowClicked(card.id) },
                    expanded = expandedCardIds.contains(card.id),
                )
            }
        }
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    card: ExpandableCardModel,
    onCardArrowClick: () -> Unit,
    expanded: Boolean,
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState)

    val cardPaddingHorizontal by transition.animateDp({
        tween(durationMillis = 1000)
    }) {
        if (expanded) 48.dp else 24.dp
    }

    val cardElevation by transition.animateDp({
        tween(durationMillis = 1000)
    }) {
        if (expanded) 24.dp else 4.dp
    }
    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        )
    }) {
        if (expanded) 0.dp else 16.dp
    }
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = 1000)
    }) {
        if (expanded) 0f else 180f
    }

    Card(
        elevation = cardElevation,
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = cardPaddingHorizontal,
                vertical = 8.dp
            )
    ) {
        Column {
            Box {
                CardArrow(
                    degrees = arrowRotationDegree,
                    onClick = onCardArrowClick
                )
                CardTitle(title = card.title)
            }
            ExpandableContent(visible = expanded, initialVisibility = expanded)
        }
    }
}

@Composable
fun CardTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}


@Composable
fun CardArrow(
    degrees: Float,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Expandable Arrow",
                modifier = Modifier.rotate(degrees),
            )
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableContent(
    visible: Boolean = true,
    initialVisibility: Boolean = false
) {
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(1000)
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(1000)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top,
            animationSpec = tween(1000)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(1000)
        )
    }
    AnimatedVisibility(
        visible = visible,
        initiallyVisible = initialVisibility,
        enter = enterTransition,
        exit = exitTransition
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Spacer(modifier = Modifier.heightIn(100.dp))
            Text(
                text = "Expandable content here",
                textAlign = TextAlign.Center
            )
        }

    }
}
