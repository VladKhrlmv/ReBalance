package com.rebalance.ui.component.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rebalance.backend.dto.SpendingDeptor
import com.rebalance.backend.localdb.entities.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupMemberSelection(
    members: List<SpendingDeptor>,
    payer: User?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    onSwitch: (Long) -> Unit
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
            value = payer?.nickname ?: "",
            onValueChange = { },
            readOnly = true,
            singleLine = true,
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
            members.forEach { member ->
                DropdownMenuItem(
                    text = { Text(member.nickname) },
                    onClick = {
                        onSwitch(member.userId)
                        expandedDropdownMembers = false
                    },
                    modifier = innerModifier
                        .fillMaxWidth()
                )
            }
        }
    }
}
