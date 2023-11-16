package com.rebalance.backend.api.entities

class ExpenseGroup {

    private var id: Long = -1

    private var name: String = ""

    private var currency: String = ""

    private var users: Set<ApplicationUser> = HashSet()

    constructor()

    constructor(id: Long, name: String, currency: String) {
        this.id = id
        this.name = name
        this.currency = currency
    }

    constructor(name: String, currency: String) {
        this.name = name
        this.currency = currency
    }

    constructor(id: Long, name: String, currency: String, usersSet: Set<ApplicationUser>) {
        this.id = id
        this.name = name
        this.currency = currency
        this.users = usersSet
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

    fun getId(): Long {
        return this.id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String {
        return this.name
    }

    fun setCurrency(currency: String) {
        this.currency = currency
    }

    fun getCurrency(): String {
        return this.currency
    }

    fun getUsers(): Set<ApplicationUser> {
        return this.users
    }

    fun setUsers(users: Set<ApplicationUser>) {
        this.users = users
    }
}
