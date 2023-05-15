package com.github.manosbatsis.services.user.service

import com.github.javafaker.Faker
import com.github.manosbatsis.services.user.exception.UserEmailDuplicatedException
import com.github.manosbatsis.services.user.exception.UserNotFoundException
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Optional

@ExtendWith(SpringExtension::class)
@Import(UserServiceImpl::class)
internal class UserServiceImplTest {
    @Autowired
    private lateinit var userService: UserService

    @MockBean
    private lateinit var userRepository: UserRepository

    private val faker = Faker()

    @Test
    fun testGetUsersWhenThereIsNone() {
        BDDMockito.given(userRepository.findAll()).willReturn(emptyList())
        val users: List<User> = userService.findAll()
        Assertions.assertThat(users).isNotNull()
        Assertions.assertThat(users).isEmpty()
    }

    @Test
    fun testGetUsersWhenThereIsOne() {
        val user = defaultUser()
        BDDMockito.given(userRepository.findAll()).willReturn(listOf(user))
        val users: List<User> = userService.findAll()
        Assertions.assertThat(users).isNotNull()
        Assertions.assertThat(users.size).isEqualTo(1)
        Assertions.assertThat(users[0]).isEqualTo(user)
    }

    @Test
    fun testSaveUser() {
        val user = defaultUser()
        BDDMockito.given(
            userRepository.save(
                ArgumentMatchers.any(
                    User::class.java,
                ),
            ),
        ).willReturn(user)
        val userSaved = userService.saveUser(user)
        Assertions.assertThat(userSaved).isNotNull()
        Assertions.assertThat(userSaved).isEqualTo(user)
    }

    @Test
    fun testDeleteUser() {
        val user = defaultUser()
        BDDMockito.willDoNothing().given(userRepository).delete(
            ArgumentMatchers.any(
                User::class.java,
            ),
        )
        userService.deleteUser(user)
        BDDMockito.then(userRepository).should().delete(
            ArgumentMatchers.any(
                User::class.java,
            ),
        )
    }

    @Test
    fun testValidateAndGetUserByIdWhenFound() {
        val user = defaultUser()
        BDDMockito.given(userRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.of(user))
        val userFound = userService.validateAndGetUserById(user.id)
        Assertions.assertThat(userFound).isNotNull()
        Assertions.assertThat(userFound).isEqualTo(user)
    }

    @Test
    fun testValidateAndGetUserByIdWhenNotFound() {
        BDDMockito.given(userRepository.findById(ArgumentMatchers.anyLong())).willReturn(Optional.empty())
        Assertions.assertThatExceptionOfType(UserNotFoundException::class.java)
            .isThrownBy { userService.validateAndGetUserById(1L) }
            .withMessage(UserNotFoundException.DEFAULT_MSG)
    }

    @Test
    fun testValidateUserExistsByEmailWhenExistent() {
        val user = defaultUser()
        BDDMockito.given(userRepository.findUserByEmail(ArgumentMatchers.anyString())).willReturn(user)
        Assertions.assertThatExceptionOfType(
            UserEmailDuplicatedException::class.java,
        )
            .isThrownBy { userService.emailAvailableOrThrow(user.email) }
        // .withMessage("User with email '" + user.email + "' already exist.")
    }

    @Test
    fun testValidateUserExistsByEmailWhenNonExistent() {
        BDDMockito.given(userRepository.findUserByEmail(ArgumentMatchers.anyString())).willReturn(null)
        userService.emailAvailableOrThrow("email@test")
        BDDMockito.then(userRepository).should().findUserByEmail(ArgumentMatchers.anyString())
    }

    private fun defaultUser(): User =
        User(
            1L,
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true,
        )
}
