package com.rebalance.ui.component.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.ui.navigation.navigateToGroup
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.MoreVertical
import compose.icons.evaicons.fill.Plus

@Composable
fun GroupContextMenu(
    navHostController: NavHostController,
    showAddGroupDialog: MutableState<Boolean>,
    groupId: Long = -1L
) {
    Column {
        val expanded = remember { mutableStateOf(false) }

        Icon(
            imageVector =
                if (groupId == -1L)
                    EvaIcons.Fill.Plus
                else
                    EvaIcons.Fill.MoreVertical,
            contentDescription = "More actions",
            modifier = Modifier
                .clickable {
                    if (groupId == -1L)
                        showAddGroupDialog.value = true
                    else
                        expanded.value = true
                }
                .padding(12.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        if (groupId != -1L) {
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
            ) {
                if (groupId != -1L) {
                    DropdownMenuItem(
                        onClick = {
                            expanded.value = false
                            navigateToGroup(navHostController, groupId)
                        },
                        text = {
                            Text("Edit group")
                        }
                    )
                }
                DropdownMenuItem(
                    onClick = {
                        expanded.value = false
                        showAddGroupDialog.value = true
                    },
                    text = {
                        Text("Create group")
                    }
                )
            }
        }
    }
}