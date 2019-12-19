package com.example.demo

data class Subscriber(
        var dbInsertEnabled: Boolean = false,
        var queue: String? = null,
        var insert: String? = null
)