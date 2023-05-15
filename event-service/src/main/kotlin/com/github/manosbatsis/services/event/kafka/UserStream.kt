package com.github.manosbatsis.services.event.kafka

import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.event.mapper.UserMapper
import com.github.manosbatsis.services.event.service.UserEventService
import com.github.manosbatsis.services.user.messages.UserEventMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import java.util.function.Consumer

@Configuration
class UserStream {
    companion object {
        private val log = loggerFor<UserStream>()
    }

    @Autowired
    private lateinit var userEventService: UserEventService

    private var userMapper = UserMapper

    @Bean
    fun users(): Consumer<Message<UserEventMessage>> {
        return Consumer<Message<UserEventMessage>> { message: Message<UserEventMessage> ->
            try {
                val userEvent = userMapper.toUserEvent(message.payload)
                log.info("Received user event, userId: {}, eventType: {}", userEvent.key.userId, userEvent.type)

                userEventService.save(userEvent)
            } catch (e: Exception) {
                log.error("An error occurred while saving userEvent {}", message, e)
                e.printStackTrace()
            }
        }
    }
}
