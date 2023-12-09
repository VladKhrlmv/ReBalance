package com.rebalance.backend.dto

data class SpendingDeptor(
    val userId: Long,
    val nickname: String,
    var selected: Boolean,
    var multiplier: Int
) {
    fun copyWithSelected(newSelected: Boolean): SpendingDeptor {
        return SpendingDeptor(userId, nickname, newSelected, multiplier)
    }
    fun copyWithMultiplier(newMultiplier: Int): SpendingDeptor {
        return SpendingDeptor(userId, nickname, selected, newMultiplier)
    }
}
