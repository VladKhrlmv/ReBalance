package com.rebalance.backend.entities


class Notification {
    private val id: Long = -1L
    private val userId: Long = -1L
    private val expenseId: Long = -1L
    private val groupId: Long = -1L
    private val amount: Double = -1.0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (expenseId != other.expenseId) return false
        if (groupId != other.groupId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + expenseId.hashCode()
        result = 31 * result + groupId.hashCode()
        return result
    }

    override fun toString(): String {
        return "Notification(id=$id, userId=$userId, expenseId=$expenseId, groupId=$groupId)"
    }

    fun getId(): Long {
        return this.id
    }

    fun getUserId(): Long {
        return this.userId
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
}
