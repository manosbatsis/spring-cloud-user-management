package com.github.manosbatsis.services.user.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import com.github.manosbatsis.lib.test.Constants
import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.messaging.MessageHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.DisabledIf
import java.io.IOException

@DisabledIf("#{environment.acceptsProfiles('avro')}")
@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.datasource.driver-class-name=org.h2.Driver",
    ],
)
@Import(
    TestChannelBinderConfiguration::class,
)
@ActiveProfiles(profiles = ["test"])
class UserStreamTest {
    @Autowired
    private lateinit var outputDestination: OutputDestination

    @Autowired
    private lateinit var userStream: UserStream

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    val faker = Faker()

    @Test
    @Throws(IOException::class)
    fun testUserCreated() {
        val createUserRequest = CreateUserRequest("email@test", "fullName", "address", true)
        val message = userStream.userCreated(1L, createUserRequest)
        val outputMessage = outputDestination.receive(0, Constants.TOPIC_NAME_USERS)
        assertThat(outputMessage).isNotNull()
        assertThat(outputMessage.headers[MessageHeaders.CONTENT_TYPE])
            .isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val userEventMessage = objectMapper.readValue(outputMessage.payload, UserEventMessage::class.java)
        assertThat(userEventMessage).isEqualTo(message.payload)
        assertThat(userEventMessage.eventType).isEqualTo(EventType.CREATED)
    }

    @Test
    @Throws(IOException::class)
    fun testUserUpdated() {
        val updateUserRequest = UpdateUserRequest(null, "address", false)
        val message = userStream.userUpdated(1L, updateUserRequest)
        val outputMessage = outputDestination.receive(0, Constants.TOPIC_NAME_USERS)
        assertThat(outputMessage).isNotNull()
        assertThat(outputMessage.headers[MessageHeaders.CONTENT_TYPE])
            .isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val userEventMessage = objectMapper.readValue(outputMessage.payload, UserEventMessage::class.java)
        assertThat(userEventMessage).isEqualTo(message.payload)
        assertThat(userEventMessage.eventType).isEqualTo(EventType.UPDATED)
    }

    @Test
    @Throws(IOException::class)
    fun testUserDeleted() {
        val user = User(
            email = faker.internet().emailAddress(),
            fullName = faker.name().fullName(),
            address = faker.address().fullAddress(),
            active = true,
        )
        val message = userStream.userDeleted(user)
        val outputMessage = outputDestination.receive(0, Constants.TOPIC_NAME_USERS)
        assertThat(outputMessage).isNotNull()
        assertThat(outputMessage.headers[MessageHeaders.CONTENT_TYPE])
            .isEqualTo(MediaType.APPLICATION_JSON_VALUE)
        val userEventMessage = objectMapper.readValue(outputMessage.payload, UserEventMessage::class.java)
        assertThat(userEventMessage).isEqualTo(message.payload)
        assertThat(userEventMessage.eventType).isEqualTo(EventType.DELETED)
    }
}
