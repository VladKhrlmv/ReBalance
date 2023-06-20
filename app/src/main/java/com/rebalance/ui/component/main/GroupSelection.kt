package com.rebalance.ui.component.main

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelection(
    preferences: PreferencesData,
    groupName: String,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    onSwitch: (Long) -> Unit
) {
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    val groupList = BackendService(preferences).getGroups()
        .filter { group -> group.getId() != preferences.groupId }

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
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = innerModifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expandedDropdownGroups,
            onDismissRequest = { expandedDropdownGroups = false },
        ) {
            groupList.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.getName()) },
                    onClick = {
                        onSwitch(group.getId())
                        expandedDropdownGroups = false
                    },
                    modifier = innerModifier
                )
            }
        }
    }
}
