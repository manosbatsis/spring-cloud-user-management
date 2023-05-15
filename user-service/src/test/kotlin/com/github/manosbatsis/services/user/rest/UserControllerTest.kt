package com.github.manosbatsis.services.user.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javafaker.Faker
import com.github.manosbatsis.services.user.exception.UserEmailDuplicatedException
import com.github.manosbatsis.services.user.exception.UserNotFoundException
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import com.github.manosbatsis.services.user.service.UserService
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@AutoConfigureMockMvc
@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.datasource.driver-class-name=org.h2.Driver",
    ],
)
@ActiveProfiles(profiles = ["test"])
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    private val faker = Faker()

    @Test
    @Throws(Exception::class)
    fun testGetUsersWhenThereIsNone() {
        BDDMockito.given<Any>(userService.findAll()).willReturn(emptyList<Any>())
        val resultActions =
            mockMvc.perform(MockMvcRequestBuilders.get(API_USERS_URL)).andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$`, Matchers.hasSize<Any>(0)))
    }

    @Test
    @Throws(Exception::class)
    fun testGetUsersWhenThereIsOne() {
        val user = defaultUser
        BDDMockito.given<Any>(userService.findAll()).willReturn(listOf<User>(user))
        val resultActions =
            mockMvc.perform(MockMvcRequestBuilders.get(API_USERS_URL)).andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath<Collection<*>>(`JSON_$`, Matchers.hasSize<Any>(1)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_0_ID`, Matchers.`is`(user.id.toInt())))
            .andExpect(MockMvcResultMatchers.jsonPath<String>(`JSON_$_0_EMAIL`, Matchers.`is`<String>(user.email)))
            .andExpect(
                MockMvcResultMatchers.jsonPath<String>(
                    `JSON_$_0_FULL_NAME`,
                    Matchers.`is`<String>(user.fullName),
                ),
            )
            .andExpect(MockMvcResultMatchers.jsonPath<Boolean>(`JSON_$_0_ACTIVE`, Matchers.`is`<Boolean>(user.active)))
    }

    @Test
    @Throws(Exception::class)
    fun testGetUserByIdWhenNonExistent() {
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willThrow(
            UserNotFoundException::class.java,
        )
        val resultActions =
            mockMvc.perform(MockMvcRequestBuilders.get(API_USERS_ID_URL, 1)).andDo(MockMvcResultHandlers.print())
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @Throws(Exception::class)
    fun testGetUserByIdWhenExistent() {
        val user = defaultUser
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willReturn(user)
        val resultActions =
            mockMvc.perform(MockMvcRequestBuilders.get(API_USERS_ID_URL, 1)).andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ID`, Matchers.`is`(user.id.toInt())))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_EMAIL`, Matchers.`is`(user.email)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_FULL_NAME`, Matchers.`is`(user.fullName)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ACTIVE`, Matchers.`is`(user.active)))
    }

    @Test
    @Throws(Exception::class)
    fun testCreateUserInformingValidInput() {
        val user = defaultUser
        BDDMockito.given(
            userService.saveUser(ArgumentMatchers.any(User::class.java) ?: user),
        ).willReturn(user)
        val createUserRequest = defaultCreateUserRequest
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(API_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(createUserRequest),
                ),
        )
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ID`, Matchers.`is`(user.id.toInt())))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_EMAIL`, Matchers.`is`(user.email)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_FULL_NAME`, Matchers.`is`(user.fullName)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ACTIVE`, Matchers.`is`(user.active)))
    }

    @Test
    @Throws(Exception::class)
    fun testCreateUserInformingInvalidInput() {
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(API_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        CreateUserRequest(),
                    ),
                ),
        )
            .andDo(MockMvcResultHandlers.print())
        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest())
    }

    @Test
    @Throws(Exception::class)
    fun testCreateUserWhenThereIsDuplicatedEmail() {
        BDDMockito.willThrow(UserEmailDuplicatedException::class.java)
            .given(userService)
            .emailAvailableOrThrow(ArgumentMatchers.anyString())
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(API_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        defaultCreateUserRequest,
                    ),
                ),
        )
            .andDo(MockMvcResultHandlers.print())
        resultActions.andExpect(MockMvcResultMatchers.status().isConflict())
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateUserWhenExistent() {
        val user = defaultUser
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willReturn(user)
        BDDMockito.given(
            userService.saveUser(
                ArgumentMatchers.any(User::class.java) ?: user,
            ),
        ).willReturn(user)
        val updateUserRequest = UpdateUserRequest("fullName2", "address2", false)
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put(API_USERS_ID_URL, user.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(updateUserRequest),
                ),
        )
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ID`, Matchers.`is`(user.id.toInt())))
            .andExpect(
                MockMvcResultMatchers.jsonPath<String?>(
                    `JSON_$_ADDRESS`,
                    Matchers.`is`<String?>(updateUserRequest.address),
                ),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath<String?>(
                    `JSON_$_FULL_NAME`,
                    Matchers.`is`<String?>(updateUserRequest.fullName),
                ),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath<Boolean?>(
                    `JSON_$_ACTIVE`,
                    Matchers.`is`<Boolean?>(updateUserRequest.active),
                ),
            )
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateUserWhenNonExistent() {
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willThrow(
            UserNotFoundException::class.java,
        )
        val resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put(API_USERS_ID_URL, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        UpdateUserRequest(
                            faker.name().fullName(),
                            faker.address().fullAddress(),
                            true,
                        ),
                    ),
                ),
        )
            .andDo(MockMvcResultHandlers.print())
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteUserWhenExistent() {
        val user = defaultUser
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willReturn(user)
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(API_USERS_ID_URL, user.id))
            .andDo(MockMvcResultHandlers.print())
        resultActions
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ID`, Matchers.`is`(user.id.toInt())))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_EMAIL`, Matchers.`is`(user.email)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_FULL_NAME`, Matchers.`is`(user.fullName)))
            .andExpect(MockMvcResultMatchers.jsonPath(`JSON_$_ACTIVE`, Matchers.`is`(user.active)))
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteUserWhenNonExistent() {
        val user = defaultUser
        BDDMockito.given(userService.validateAndGetUserById(ArgumentMatchers.anyLong())).willThrow(
            UserNotFoundException::class.java,
        )
        val resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(API_USERS_ID_URL, user.id))
            .andDo(MockMvcResultHandlers.print())
        resultActions.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    private val defaultUser: User
        private get() {
            val user = User(1L, "email@test", "fullName", "address", true)
            return user
        }
    private val defaultCreateUserRequest: CreateUserRequest
        private get() = CreateUserRequest("email@test", "fullName", "address", true)

    companion object {
        private const val API_USERS_URL = "/api/users"
        private const val API_USERS_ID_URL = "/api/users/{id}"
        private const val `JSON_$` = "$"
        private const val `JSON_$_ID` = "$.id"
        private const val `JSON_$_EMAIL` = "$.email"
        private const val `JSON_$_FULL_NAME` = "$.fullName"
        private const val `JSON_$_ADDRESS` = "$.address"
        private const val `JSON_$_ACTIVE` = "$.active"
        private const val `JSON_$_0_ID` = "$[0].id"
        private const val `JSON_$_0_EMAIL` = "$[0].email"
        private const val `JSON_$_0_FULL_NAME` = "$[0].fullName"
        private const val `JSON_$_0_ACTIVE` = "$[0].active"
    }
}
