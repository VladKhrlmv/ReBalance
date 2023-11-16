package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rebalance.PreferencesData
import com.rebalance.backend.api.entities.ApplicationUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupMemberSelection(
    preferences: PreferencesData,
    memberSet: Set<ApplicationUser>,
    memberName: String,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    onSwitch: (ApplicationUser) -> Unit
) {
    var expandedDropdownMembers by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandedDropdownMembers,
        onExpandedChange = {
            expandedDropdownMembers = !expandedDropdownMembers
        },
        modifier = modifier
    ) {
        TextField(
            value = memberName,
            onValueChange = { },
            readOnly = true,
            label = {
                Text(text = "Payer")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandedDropdownMembers
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            modifier = innerModifier
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expandedDropdownMembers,
            onDismissRequest = { expandedDropdownMembers = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            memberSet.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.getUsername()) },
                    onClick = {
                        onSwitch(member)
                        expandedDropdownMembers = false
                    },
                    modifier = innerModifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
