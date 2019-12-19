package com.example.demo

import org.jooq.DSLContext
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class SubscribeService(val subscribers: List<Subscriber>,
                       val rabbitTemplate: RabbitTemplate,
                       val dsl: DSLContext) {
    @PostConstruct
    fun subscribeList() {
        val admin = RabbitAdmin(this.rabbitTemplate.connectionFactory)
        try {
            for (subscriber in subscribers) {
                if (subscriber.queue.isNullOrEmpty() || subscriber.insert.isNullOrEmpty()) continue;
                val container = SimpleMessageListenerContainer()
                container.connectionFactory = this.rabbitTemplate.connectionFactory

                val que = Queue(subscriber.queue)
                admin.declareQueue(que)
                container.addQueues(que)
                container.setMessageListener(MessageListenerAdapter(ConsumerHandler(dsl, subscriber.queue!!, subscriber.insert!!)))
                container.start()

                println("Started listening queue[${subscriber.queue}] with query: ${subscriber.insert}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}