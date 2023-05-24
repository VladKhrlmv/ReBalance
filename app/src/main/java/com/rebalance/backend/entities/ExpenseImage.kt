package com.rebalance.backend.entities

class ExpenseImage {

    private var image: String? = null

    constructor()

    constructor(image: String?) {
        this.image = image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getImage(): String? {
        return this.image
    }
}