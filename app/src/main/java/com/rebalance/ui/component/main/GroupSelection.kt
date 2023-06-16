package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rebalance.PreferencesData
import com.rebalance.backend.service.BackendService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelection(
    preferences: PreferencesData,
    groupName: String,
    onSwitch: (Long) -> Unit
) {
    var expandedDropdownGroups by remember { mutableStateOf(false) }
    val groupList = BackendService(preferences).getGroups()
        .filter { group -> group.getId() != preferences.groupId }

    ExposedDropdownMenuBox(
        expanded = expandedDropdownGroups,
        onExpandedChange = {
            expandedDropdownGroups = !expandedDropdownGroups
        }
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
            modifier = Modifier
                .menuAnchor()
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 100.dp)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
    }
}
