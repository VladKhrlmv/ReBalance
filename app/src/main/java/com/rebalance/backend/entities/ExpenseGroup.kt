package com.rebalance.backend.entities

class ExpenseGroup {

    private var id: Long = -1

    private var name: String = ""

    private var currency: String = ""

    public constructor()

    constructor(id: Long, name: String, currency: String) {
        this.id = id
        this.name = name
        this.currency = currency
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseGroup

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ExpenseGroup(id=$id, name='$name', currency='$currency')"
    }

    public fun getId(): Long {
        return this.id
    }

    public fun setId(id: Long) {
        this.id = id
    }

    public fun setName(name: String) {
        this.name = name
    }

    public fun getName(): String {
        return this.name
    }

    public fun setCurrency(currency: String) {
        this.currency = currency
    }

    public fun getCurrency(): String {
        return this.currency
    }


}