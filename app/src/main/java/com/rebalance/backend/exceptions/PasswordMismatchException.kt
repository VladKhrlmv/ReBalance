package com.rebalance.backend.exceptions

class PasswordMismatchException : RuntimeException {
    constructor() : super("Passwords are different")
    constructor(message: String) : super(message)
}
