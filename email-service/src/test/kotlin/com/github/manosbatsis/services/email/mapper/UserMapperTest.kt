package com.github.manosbatsis.services.email.mapper

import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Date
import java.util.UUID

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class UserMapperTest {

    private var userMapper: UserMapper = UserMapper

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
        val userEvent = userMapper.toUserEvent(userEventMessage)
        Assertions.assertThat(userEvent).isNotNull()
        Assertions.assertThat(userEvent.key.userId).isEqualTo(userId)
        Assertions.assertThat(userEvent.key.datetime).isEqualTo(datetime)
        Assertions.assertThat(userEvent.data).isEqualTo(userJson)
        Assertions.assertThat(userEvent.type).isEqualTo(eventType.name)
    }
}
