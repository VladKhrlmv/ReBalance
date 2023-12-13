package com.rebalance.backend.api.dto.response

enum class ApiNotificationType(private val value: Int) {
    UserAddedToGroup(0),
    CurrentUserAddedToGroup(1),
    GroupCreated(2),
    GroupExpenseAdded(3),
    GroupExpenseEdited(4),
    GroupExpenseDeleted(5),
    PersonalExpenseAdded(6),
    PersonalExpenseEdited(7),
    PersonalExpenseDeleted(8);

    companion object {
        private val map = values().associateBy(ApiNotificationType::value)
        fun fromInt(type: Int) = map[type]
    }
}
