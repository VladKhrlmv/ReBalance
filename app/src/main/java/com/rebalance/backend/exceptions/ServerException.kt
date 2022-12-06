package com.rebalance.backend.exceptions

class ServerException : RuntimeException {
    constructor() : super("Server exception occurred")
    constructor(message: String) : super(message)
}