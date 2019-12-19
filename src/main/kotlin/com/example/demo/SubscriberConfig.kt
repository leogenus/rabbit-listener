package com.example.demo

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "subscriber")
@EnableConfigurationProperties
class SubscriberConfig(var enabled: Boolean = false, var list: List<Subscriber> = listOf())