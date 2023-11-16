package com.rebalance.backend.api.entities


class Notification {
    private val id: Long = -1L
    private val userId: Long = -1L
    private val userFromId: Long = -1L
    private val expenseId: Long = -1L
    private val groupId: Long = -1L
    private val amount: Double = -1.0



    fun getId(): Long {
        return this.id
    }

    fun getUserId(): Long {
        return this.userId
    }

    fun getUserFromId(): Long {
        return this.userFromId
    }

    fun getExpenseId(): Long {
        return this.expenseId
    }

    fun getGroupId(): Long {
        return this.groupId
    }

    fun getAmount(): Double {
        return this.amount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (userFromId != other.userFromId) return false
        if (expenseId != other.expenseId) return false
        if (groupId != other.groupId) return false
        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + userFromId.hashCode()
        result = 31 * result + expenseId.hashCode()
        result = 31 * result + groupId.hashCode()
        result = 31 * result + amount.hashCode()
        return result
    }

    override fun toString(): String {
        return "Notification(id=$id, userId=$userId, userFromId=$userFromId, expenseId=$expenseId, groupId=$groupId, amount=$amount)"
    }
}
