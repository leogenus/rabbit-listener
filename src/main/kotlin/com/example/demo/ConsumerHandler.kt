package com.example.demo

import org.apache.commons.logging.LogFactory
import org.jooq.DSLContext
import java.nio.charset.StandardCharsets

class ConsumerHandler(private val dsl: DSLContext,
                      private val dbInsertEnabled: Boolean,
                      private val name: String, private val insertSql: String) {
    private val log = LogFactory.getLog(javaClass)!!

    fun handleMessage(bytes: ByteArray) {
        try {
            val message = String(bytes, StandardCharsets.UTF_8)
            if (dbInsertEnabled)
                dsl.execute(insertSql, message)

            log.info("Queue[${name}] message: $message")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}