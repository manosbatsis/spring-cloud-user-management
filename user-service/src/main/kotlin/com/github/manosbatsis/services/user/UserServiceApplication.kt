package com.github.manosbatsis.services.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class UserServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(UserServiceApplication::class.java, *args)
}
