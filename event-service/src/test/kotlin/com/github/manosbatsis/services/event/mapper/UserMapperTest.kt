package com.github.manosbatsis.services.event.mapper

import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.UUID

class UserMapperTest {

    private var userMapper: UserMapper = UserMapper

    @Test
    fun testToUserEventResponse() {
        val userId = 1L
        val datetime = Date()
        val data = "data"
        val type = "type"
        val userEvent = UserEvent(UserEventKey(userId, datetime), type, data)
        val userEventResponse = userMapper.toUserEventResponse(userEvent)
        assertThat(userEventResponse).isNotNull()
        assertThat(userEventResponse.userId).isEqualTo(userId)
        assertThat(userEventResponse.datetime).isEqualTo(datetime)
        assertThat(userEventResponse.data).isEqualTo(data)
        assertThat(userEventResponse.type).isEqualTo(type)
    }

    @Test
    fun testCreateUserEvent() {
        val eventId = UUID.randomUUID().toString()
        val datetime = Date()
        val eventType = EventType.CREATED
        val userId = 1L
        val userJson = "{\"email\":\"email\",\"fullName\":\"fullName\",\"active\":true}"
        val userEventMessage = UserEventMessage.newBuilder()
            .setEventId(eventId)
            .setEventTimestamp(datetime.time)
            .setEventType(eventType)
            .setUserId(userId)
            .setUserJson(userJson)
            .build()
        val userEvent = userMapper!!.toUserEvent(userEventMessage)
        Assertions.assertThat(userEvent).isNotNull()
        assertThat(userEvent.key.userId).isEqualTo(userId)
        assertThat(userEvent.key.datetime).isEqualTo(datetime)
        assertThat(userEvent.data).isEqualTo(userJson)
        assertThat(userEvent.type).isEqualTo(eventType.name)
    }
}
