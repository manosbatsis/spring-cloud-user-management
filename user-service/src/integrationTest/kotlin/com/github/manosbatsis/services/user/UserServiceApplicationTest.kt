package com.github.manosbatsis.services.user

import com.github.javafaker.Faker
import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.lib.test.error.ErrorResponse
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.repository.UserRepository
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UserResponse
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration
import java.util.Arrays

@Testcontainers
@ActiveProfiles("docker")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserServiceApplicationTest : AbstractTestcontainers() {
    companion object {
        private const val API_USERS_URL = "/api/users"
        private const val API_USERS_USER_ID_URL = "/api/users/%s"
        private val AT_MOST_DURATION = Duration.ofSeconds(12)
        private val POLL_INTERVAL_DURATION = Duration.ofSeconds(1)
        private val log = loggerFor<UserServiceApplicationTest>()
    }

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var userRepository: UserRepository
    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    @Order(1)
    fun testGetUsersWhenThereIsNone() {
        val responseEntity = testRestTemplate.getForEntity(API_USERS_URL, Array<UserResponse>::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body).isEmpty()
    }

    @Test
    @Order(2)
    fun testGetUsersWhenThereIsOne() {
        val user = userRepository.save(
            defaultUser(),
        )
        val responseEntity = testRestTemplate.getForEntity(API_USERS_URL, Array<UserResponse>::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body).hasSize(1)
        assertThat(responseEntity.body?.first()?.id).isEqualTo(user.id)
        assertThat(responseEntity.body?.first()?.email).isEqualTo(user.email)
        assertThat(responseEntity.body?.first()?.active).isEqualTo(user.active)
    }

    @Test
    @Order(3)
    fun testGetUserWhenNonExistent() {
        val id = 999L
        val url = String.format(API_USERS_USER_ID_URL, id)
        val responseEntity = testRestTemplate.getForEntity(url, ErrorResponse::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body?.message).isEqualTo("User does not exist.")
    }

    @Test
    @Order(4)
    fun testGetUserWhenExistent() {
        val user = userRepository.save(
            defaultUser(),
        )
        val url = String.format(API_USERS_USER_ID_URL, user.id)
        val responseEntity = testRestTemplate.getForEntity(url, UserResponse::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body?.id).isEqualTo(user.id)
        assertThat(responseEntity.body?.email).isEqualTo(user.email)
        assertThat(responseEntity.body?.fullName).isEqualTo(user.fullName)
        assertThat(responseEntity.body?.active).isEqualTo(user.active)
    }

    @Test
    @Order(5)
    fun testCreateUser() {
        val createUserRequest = defaultCreateUserRequest()
        val responseEntity = testRestTemplate.postForEntity(
            API_USERS_URL,
            createUserRequest,
            UserResponse::class.java,
        )
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body?.id).isPositive()
        assertThat(responseEntity.body?.email).isEqualTo(createUserRequest.email)
        assertThat(responseEntity.body?.fullName).isEqualTo(createUserRequest.fullName)
        assertThat(responseEntity.body?.active).isEqualTo(createUserRequest.active)
        val userId = responseEntity.body?.id ?: 0
        val userFound = userRepository.findById(userId)
        assertThat(userFound).isPresent()
        Awaitility.await().atMost(AT_MOST_DURATION)
            .pollInterval(POLL_INTERVAL_DURATION)
            .untilAsserted {
                UserServiceApplicationTest.log.info(
                    "Waiting for event-service to receive the message and process ...",
                )
                val eventServiceUrl = String.format(
                    "%s/events?userId=%s",
                    EVENT_SERVICE_API_URL,
                    userId,
                )
                val eventServiceResponseEntity = testRestTemplate.getForEntity(
                    eventServiceUrl,
                    Array<EventServiceUserEventResponse>::class.java,
                )
                assertThat(eventServiceResponseEntity.statusCode)
                    .isEqualTo(HttpStatus.OK)
                assertThat(eventServiceResponseEntity.body).isNotNull()
                assertThat(
                    Arrays.stream(eventServiceResponseEntity.body)
                        .anyMatch { (_, _, type): EventServiceUserEventResponse ->
                            (
                                type
                                    == "CREATED"
                                )
                        },
                )
                    .isTrue()
            }
    }

    @Test
    @Order(6)
    fun testUpdateUser() {
        val user = userRepository.save(
            defaultUser(),
        )
        val userId = user.id
        val updateUserRequest = UpdateUserRequest(active = false)
        val requestUpdate = HttpEntity(updateUserRequest)
        val url = String.format(API_USERS_USER_ID_URL, userId)
        val responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, UserResponse::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body?.id).isEqualTo(userId)
        assertThat(responseEntity.body?.email).isEqualTo(user.email)
        assertThat(responseEntity.body?.fullName).isEqualTo(user.fullName)
        assertThat(responseEntity.body?.active).isEqualTo(updateUserRequest.active)
        Awaitility.await().atMost(AT_MOST_DURATION)
            .pollInterval(POLL_INTERVAL_DURATION)
            .untilAsserted {
                UserServiceApplicationTest.log.info(
                    "Waiting for event-service to receive the message and process ...",
                )
                val eventServiceUrl = String.format(
                    "%s/events?userId=%s",
                    EVENT_SERVICE_API_URL,
                    userId,
                )
                val eventServiceResponseEntity = testRestTemplate.getForEntity(
                    eventServiceUrl,
                    Array<EventServiceUserEventResponse>::class.java,
                )
                assertThat(eventServiceResponseEntity.statusCode)
                    .isEqualTo(HttpStatus.OK)
                assertThat(eventServiceResponseEntity.body).isNotNull()
                assertThat(
                    Arrays.stream(eventServiceResponseEntity.body)
                        .anyMatch { (_, _, type): EventServiceUserEventResponse ->
                            (
                                type
                                    == "UPDATED"
                                )
                        },
                )
                    .isTrue()
            }
    }

    @Test
    @Order(7)
    fun testDeleteUser() {
        log.info("testDeleteUser...")
        val user = userRepository.save(
            defaultUser(),
        )
        val userId = user.id
        val url = String.format(API_USERS_USER_ID_URL, userId)
        val responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, UserResponse::class.java)
        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseEntity.body).isNotNull()
        assertThat(responseEntity.body!!.id).isEqualTo(userId)
        assertThat(responseEntity.body.email).isEqualTo(user.email)
        assertThat(responseEntity.body.fullName).isEqualTo(user.fullName)
        assertThat(responseEntity.body.active).isEqualTo(user.active)
        val userNotFound = userRepository.findById(userId)
        assertThat(userNotFound).isNotPresent()
        Awaitility.await().atMost(AT_MOST_DURATION)
            .pollInterval(POLL_INTERVAL_DURATION)
            .untilAsserted {
                log.info(
                    "Waiting for event-service to receive the message and process ...",
                )
                val eventServiceUrl = String.format(
                    "%s/events?userId=%s",
                    EVENT_SERVICE_API_URL,
                    userId,
                )
                val eventServiceResponseEntity = testRestTemplate.getForEntity(
                    eventServiceUrl,
                    Array<EventServiceUserEventResponse>::class.java,
                )
                assertThat(eventServiceResponseEntity.statusCode)
                    .isEqualTo(HttpStatus.OK)
                assertThat(eventServiceResponseEntity.body).isNotNull()
                val userIds = eventServiceResponseEntity.body.map { it.userId }.distinct()
                val types = eventServiceResponseEntity.body.map { it.type }.distinct()
                log.info(
                    "testDeleteUser, found {} messages, userIds: ({}), types: ({})",
                    eventServiceResponseEntity.body.size,
                    userIds.joinToString(", "),
                    types.joinToString(", "),
                )
                assertThat(
                    Arrays.stream(eventServiceResponseEntity.body)
                        .anyMatch { (_, _, type): EventServiceUserEventResponse ->
                            type == "DELETED"
                        },
                )
                    .isTrue()
            }
    }

    private fun defaultUser(): User =
        User(
            email = faker.internet().emailAddress(),
            fullName = faker.name().fullName(),
            address = faker.address().fullAddress(),
            active = true,
        )
    private fun defaultCreateUserRequest(): CreateUserRequest =
        CreateUserRequest(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true,
        )
    private data class EventServiceUserEventResponse(
        val userId: Long,
        val datetime: String,
        val type: String,
        val data: String,
    )
}
