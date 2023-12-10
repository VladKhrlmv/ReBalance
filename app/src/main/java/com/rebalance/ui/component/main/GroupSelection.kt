package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rebalance.backend.localdb.entities.Group
import com.rebalance.backend.service.BackendService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelection(
    group: Group?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    onSwitch: (Long) -> Unit
) {
    val backendService = BackendService.get()

    var expandedDropdownGroups by remember { mutableStateOf(false) }
    var groupList by remember { mutableStateOf(listOf<Group>()) }

    LaunchedEffect(group) {
        groupList = backendService.getUserGroups()
    }

    ExposedDropdownMenuBox(
        expanded = expandedDropdownGroups,
        onExpandedChange = {
            expandedDropdownGroups = !expandedDropdownGroups
        },
        modifier = modifier
    ) {
        TextField(
            value = group?.name ?: "",
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            label = {
                Text(text = "Group")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandedDropdownGroups
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = innerModifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expandedDropdownGroups,
            onDismissRequest = { expandedDropdownGroups = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            groupList.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        onSwitch(group.id)
                        expandedDropdownGroups = false
                    },
                    modifier = innerModifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
