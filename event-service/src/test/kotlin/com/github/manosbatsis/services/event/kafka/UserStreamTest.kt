package com.github.manosbatsis.services.event.kafka

import com.github.manosbatsis.lib.test.Constants
import com.github.manosbatsis.services.event.repository.UserEventRepository
import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.CassandraContainer
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.Date
import java.util.UUID

@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
@Import(
    TestChannelBinderConfiguration::class,
)
class UserStreamTest {
    companion object {

        @JvmStatic
        val network: Network = Network.newNetwork()

        @Container
        @JvmStatic
        private val cassandraContainer: CassandraContainer<*> = CassandraContainer("cassandra:4.1.0")
            .withNetwork(network)

        @JvmStatic
        @DynamicPropertySource
        private fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.cassandra.contact-points") {
                // cassandraContainer.host
                "localhost:${cassandraContainer.getMappedPort(9042)}"
            }
        }
    }

    @Autowired
    private lateinit var inputDestination: InputDestination

    @Autowired
    private lateinit var userEventRepository: UserEventRepository

    @Test
    fun testUsers() {
        val eventId = UUID.randomUUID().toString()
        val datetime = Date()
        val eventType = EventType.CREATED
        val userId = 1L
        val userJson = "{\"email\":\"email\",\"fullName\":\"fullName\",\"address\":\"address\",\"active\":true}"
        val userEventMessage = UserEventMessage.newBuilder()
            .setEventId(eventId)
            .setEventTimestamp(datetime.time)
            .setEventType(eventType)
            .setUserId(userId)
            .setUserJson(userJson)
            .build()
        inputDestination.send(
            MessageBuilder.withPayload(userEventMessage).build(),
            Constants.TOPIC_NAME_USERS,
        )
        val userEvents = userEventRepository.findByKeyUserId(userId)
        assertThat(userEvents).isNotNull()
        assertThat(userEvents.size).isEqualTo(1)
        assertThat(userEvents.first().key.userId).isEqualTo(userId)
        assertThat(userEvents.first().key.datetime).isEqualTo(datetime)
        assertThat(userEvents.first().data).isEqualTo(userJson)
        assertThat(userEvents.first().type).isEqualTo(eventType.name)
    }
}
