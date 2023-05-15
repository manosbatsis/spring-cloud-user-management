package com.github.manosbatsis.services.event

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class EventServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(EventServiceApplication::class.java, *args)
}
