package com.rebalance.backend.entities

class Expense {

    private var id: Long = -1
    private var amount: Int = 0;

    private var dateStamp: String = "2000-01-01"
    private var category: String = "default"
    private var description: String = ""
    private var globalId: Long = -1

    public constructor()

    constructor(
        id: Long,
        amount: Int,
        dateStamp: String,
        category: String,
        description: String,
        globalId: Long
    ) {
        this.id = id
        this.amount = amount
        this.dateStamp = dateStamp
        this.category = category
        this.description = description
        this.globalId = globalId
    }

    constructor(amount: Int, dateStamp: String, category: String, description: String) {
        this.amount = amount
        this.dateStamp = dateStamp
        this.category = category
        this.description = description
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Expense

        if (id != other.id) return false
        if (globalId != other.globalId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + globalId.hashCode()
        return result
    }

    public fun getId(): Long {
        return this.id
    }

    public fun getAmount(): Int {
        return this.amount
    }

    public fun getDescription(): String {
        return this.description
    }

    public fun getGlobalId(): Long {
        return this.globalId
    }

    public fun setId(id: Long) {
        this.id = id
    }

    public fun setAmount(amount: Int) {
        this.amount = amount
    }

    public fun setDescription(description: String) {
        this.description = description
    }

    public fun setGlobalId(globalId: Long) {
        this.globalId = globalId
    }


    public fun getDateStamp(): String {
        return dateStamp
    }

    override fun toString(): String {
        return "Expense(id=$id, amount=$amount, dateStamp='$dateStamp', category='$category', description='$description', globalId=$globalId)"
    }

    public fun getCategory(): String {
        return category
    }

    public fun setCategory() {
        this.category = category
    }

}