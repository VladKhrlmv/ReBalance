package com.rebalance.backend.entities

class ApplicationUser {

    private var id: Long = -1
    private var username: String = ""
    private var email: String = ""

    public constructor()

    public constructor(id: Long, username: String, email: String) {
        this.id = id
        this.username = username
        this.email = email
    }

    constructor(username: String, email: String) {
        this.username = username
        this.email = email
    }

    override fun toString(): String {
        return "ApplicationUser(id=$id, username='$username', email='$email')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApplicationUser

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    public fun getUsername(): String {
        return this.username
    }

    public fun getEmail(): String {
        return this.email
    }

    public fun getId(): Long{
        return this.id
    }

}


