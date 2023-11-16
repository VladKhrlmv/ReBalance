package com.rebalance.backend.api.entities

class LoginAndPassword {

    private var email: String = ""
    private var password: String = ""

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }

    constructor()

    override fun toString(): String {
        return "LoginAndPassword(email='$email', password='$password')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginAndPassword

        if (email != other.email) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = email.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }

    fun getEmail(): String {
        return email
    }

    fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }


    fun setEmail(email: String) {
        this.email = email
    }

}
