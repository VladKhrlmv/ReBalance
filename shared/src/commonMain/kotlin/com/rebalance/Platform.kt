package com.rebalance

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform