package com.example.demo

import org.apache.commons.logging.LogFactory
import org.jooq.DSLContext
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class SubscribeService(val subscriberConfig: SubscriberConfig,
                       val rabbitTemplate: RabbitTemplate,
                       val dsl: DSLContext) {
    val log = LogFactory.getLog(javaClass)!!

    @PostConstruct
    fun subscribeList() {
        if (!subscriberConfig.enabled) return

        val admin = RabbitAdmin(this.rabbitTemplate.connectionFactory)
        try {
            for (subscriber in subscriberConfig.list) {
                if (subscriber.queue.isNullOrEmpty() || subscriber.insert.isNullOrEmpty()) continue;
                val container = SimpleMessageListenerContainer()
                container.connectionFactory = this.rabbitTemplate.connectionFactory

                val que = Queue(subscriber.queue)
                admin.declareQueue(que)
                container.addQueues(que)
                container.setMessageListener(MessageListenerAdapter(
                        ConsumerHandler(dsl,
                                subscriber.dbInsertEnabled,
                                subscriber.queue!!,
                                subscriber.insert!!)))
                container.start()

                log.info("Started listening queue[${subscriber.queue}] with query: ${subscriber.insert}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}