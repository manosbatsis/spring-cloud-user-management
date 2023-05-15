package com.github.manosbatsis.services.user.kafka

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.user.exception.UserStreamJsonProcessingException
import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import java.util.UUID

@Component
class UserStream(
    val streamBridge: StreamBridge,
    val objectMapper: ObjectMapper,
) {
    companion object {
        private val log = loggerFor<UserStream>()
    }

    @Value("\${spring.cloud.stream.bindings.users-out-0.content-type:application/json}")
    private lateinit var streamOutMimeType: String

    fun userCreated(userId: Long, createUserRequest: CreateUserRequest): Message<UserEventMessage> {
        return sendToBus(userId, EventType.CREATED, writeValueAsString(createUserRequest))
    }

    fun userCreated(user: User): Message<UserEventMessage> {
        return sendToBus(user.id, EventType.CREATED, writeValueAsString(user))
    }

    fun userUpdated(userId: Long, updateUserRequest: UpdateUserRequest): Message<UserEventMessage> {
        return sendToBus(userId, EventType.UPDATED, writeValueAsString(updateUserRequest))
    }

    fun userUpdated(user: User): Message<UserEventMessage> {
        return sendToBus(user.id, EventType.UPDATED, writeValueAsString(user))
    }

    fun userDeleted(user: User): Message<UserEventMessage> {
        return sendToBus(user.id, EventType.DELETED, writeValueAsString(user))
    }

    private fun sendToBus(userId: Long, eventType: EventType, json: String?): Message<UserEventMessage> {
        val userEventMessage = UserEventMessage(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            eventType,
            userId,
            json,
        )
        return sendToBus(userId, userEventMessage)
    }

    private fun sendToBus(
        partitionKey: Long,
        userEventMessage: UserEventMessage,
    ): Message<UserEventMessage> {
        log.info("sendToBus, userId: {}, eventType: {}", userEventMessage.userId, userEventMessage.eventType)
        val message = MessageBuilder.withPayload(userEventMessage)
            .setHeader("partitionKey", partitionKey)
            .setHeader("target-protocol", "kafka")
            .build()
        streamBridge.send("users-topic", message, MimeType.valueOf(streamOutMimeType))
        return message
    }

    private fun writeValueAsString(`object`: Any): String {
        return try {
            objectMapper.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            throw UserStreamJsonProcessingException(e)
        }
    }
}
