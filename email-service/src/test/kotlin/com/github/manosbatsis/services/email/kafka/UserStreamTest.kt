package com.github.manosbatsis.services.email.kafka

import com.github.javafaker.Faker
import com.github.manosbatsis.lib.test.Constants
import com.github.manosbatsis.services.user.messages.EventType
import com.github.manosbatsis.services.user.messages.UserEventMessage
import com.icegreen.greenmail.configuration.GreenMailConfiguration
import com.icegreen.greenmail.junit5.GreenMailExtension
import com.icegreen.greenmail.util.GreenMailUtil
import com.icegreen.greenmail.util.ServerSetupTest
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.Date
import java.util.UUID

/** This class has the same test as [UserStreamTest] using a different style  */
@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
@Import(
    TestChannelBinderConfiguration::class,
)
class UserStreamTest {

    @Autowired
    private lateinit var inputDestination: InputDestination
    private val faker = Faker()

    @Test
    fun testEmail() {
        val eventId = UUID.randomUUID().toString()
        val datetime = Date()
        val eventType = EventType.CREATED
        val userId = 1L
        val expectedEmailAddress = faker.internet().emailAddress()
        val userJson = (
            "{\"email\":\"" +
                expectedEmailAddress +
                "\",\"fullName\":\"fullName\",\"address\":\"address\",\"active\":true}"
            )
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
        Awaitility.await().atMost(AT_MOST_DURATION)
            .untilAsserted {
                val receivedMessages = greenMail.receivedMessages
                Assertions.assertEquals(1, receivedMessages.size)
                val receivedMessage = receivedMessages[0]
                Assertions.assertTrue(
                    GreenMailUtil.getBody(receivedMessage)
                        .contains("Your new account"),
                )
                Assertions.assertEquals(1, receivedMessage.allRecipients.size)
                Assertions.assertEquals(
                    expectedEmailAddress,
                    receivedMessage.allRecipients[0].toString(),
                )
            }
    }

    companion object {
        val AT_MOST_DURATION = Duration.ofSeconds(10)

        @JvmField
        @RegisterExtension
        public var greenMail = GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(
                GreenMailConfiguration.aConfig().withUser("user", "password"),
            )
            .withPerMethodLifecycle(false)
    }
}
