package com.rebalance.backend.entities

class Expense {

    private var id: Long = -1
    private var amount: Double = 0.0

    private var dateStamp: String = "2000-01-01"
    private var category: String = "default"
    private var description: String = ""
    private var globalId: Long = -1

    constructor()

    constructor(
        id: Long,
        amount: Double,
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

    constructor(amount: Double, dateStamp: String, category: String, description: String, globalId: Long) {
        this.amount = amount
        this.dateStamp = dateStamp
        this.category = category
        this.description = description
        this.globalId = globalId
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

    fun getId(): Long {
        return this.id
    }

    fun getAmount(): Double {
        return this.amount
    }

    fun getDescription(): String {
        return this.description
    }

    fun getGlobalId(): Long {
        return this.globalId
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun setAmount(amount: Double) {
        this.amount = amount
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setGlobalId(globalId: Long) {
        this.globalId = globalId
    }


    fun getDateStamp(): String {
        return dateStamp
    }

    override fun toString(): String {
        return "Expense(id=$id, amount=$amount, dateStamp='$dateStamp', category='$category', description='$description', globalId=$globalId)"
    }

    fun getCategory(): String {
        return category
    }

    fun setCategory(category: String) {
        this.category = category
    }

}