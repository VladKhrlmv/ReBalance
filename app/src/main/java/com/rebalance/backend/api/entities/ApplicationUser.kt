package com.rebalance.backend.api.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ApplicationUser() : Parcelable {

    private var id: Long = -1
    private var username: String = ""
    private var email: String = ""
    private var password: String = ""


    constructor(id: Long, username: String, email: String) : this() {
        this.id = id
        this.username = username
        this.email = email
    }

    constructor(username: String, email: String) : this() {
        this.username = username
        this.email = email
    }

    constructor(username: String, email: String, password: String) : this() {
        this.username = username
        this.email = email
        this.password = password
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

    fun getUsername(): String {
        return this.username
    }

    fun getEmail(): String {
        return this.email
    }

    fun getId(): Long {
        return this.id
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

}


