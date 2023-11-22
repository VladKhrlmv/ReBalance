package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rebalance.backend.service.BackendService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelection(
    backendService: BackendService,
    groupName: String,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    onSwitch: (Long) -> Unit
) {
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    val groupList = backendService.getGroups()
        .filter { group -> group.getId() != backendService.getGroupId() }

    ExposedDropdownMenuBox(
        expanded = expandedDropdownGroups,
        onExpandedChange = {
            expandedDropdownGroups = !expandedDropdownGroups
        },
        modifier = modifier
    ) {
        TextField(
            value = groupName,
            onValueChange = { },
            readOnly = true,
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
                    text = { Text(group.getName()) },
                    onClick = {
                        onSwitch(group.getId())
                        expandedDropdownGroups = false
                    },
                    modifier = innerModifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
