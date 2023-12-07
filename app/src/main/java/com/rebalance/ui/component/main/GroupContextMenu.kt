package com.rebalance.ui.component.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rebalance.ui.navigation.navigateToGroup
import compose.icons.EvaIcons
import compose.icons.evaicons.Fill
import compose.icons.evaicons.fill.MoreVertical

@Composable
fun GroupContextMenu(
    navHostController: NavHostController,
    groupId: Long = -1L,
    onCreateGroupClick: () -> Unit
) {
    Column {
        var expanded by remember { mutableStateOf(false) }

        Icon(
            imageVector = EvaIcons.Fill.MoreVertical,
            contentDescription = "More actions",
            modifier = Modifier
                .clickable { expanded = true }
                .padding(12.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (groupId != -1L) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        navigateToGroup(navHostController, groupId)
                    },
                    text = {
                        Text("Edit group")
                    }
                )
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onCreateGroupClick()
                },
                text = {
                    Text("Create group")
                }
            )
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    //TODO: implement
                },
                text = {
                    Text("Export to Excel")
                }
            )
        }
    }
}
