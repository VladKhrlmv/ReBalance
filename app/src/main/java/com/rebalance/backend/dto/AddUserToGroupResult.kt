package com.rebalance.backend.dto

enum class AddUserToGroupResult {
    Placeholder,
    Added,
    UserNotFound,
    UserInGroup,
    CannotSaveUser,
    ServerError
}
