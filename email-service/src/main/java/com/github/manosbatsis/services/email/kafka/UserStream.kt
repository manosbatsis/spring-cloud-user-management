package com.github.manosbatsis.services.email.kafka

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.email.mapper.UserMapper
import com.github.manosbatsis.services.email.model.EmailRecepient
import com.github.manosbatsis.services.email.model.UserEvent
import com.github.manosbatsis.services.email.service.EmailService
import com.github.manosbatsis.services.user.messages.UserEventMessage
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class UserStream(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private val log = loggerFor<UserStream>()
    }

    private val userMapper = UserMapper

    @Bean
    fun users(): Consumer<Message<UserEventMessage>> {
        return Consumer<Message<UserEventMessage>> { message: Message<UserEventMessage> ->
            log.warn("users, payload: {}", message.payload)
            val userEvent = userMapper.toUserEvent(message.payload)
            if (userEvent.type == "CREATED") {
                val emailRecepient = getEmailRecepient(userEvent)
                emailService.sendRegistrationConfirmation(emailRecepient)
            } else {
                log.debug("Ignored user event: \n{}\n", userEvent.type)
            }
        }
    }

    private fun getEmailRecepient(userEvent: UserEvent): EmailRecepient {
        return try {
            objectMapper.readValue(userEvent.data, EmailRecepient::class.java)
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
