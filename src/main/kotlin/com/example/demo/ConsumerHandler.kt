package com.example.demo

import org.jooq.DSLContext
import java.nio.charset.StandardCharsets

class ConsumerHandler(private val dsl: DSLContext, val name: String,  private val insertSql: String) {
    fun handleMessage(bytes: ByteArray) {
        try {
            val message = String(bytes, StandardCharsets.UTF_8)
            dsl.execute(insertSql, message)
            println("Queue[${name}] message: $message")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}