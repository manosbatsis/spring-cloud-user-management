package com.github.manosbatsis.services.user.mapper

import com.github.javafaker.Faker
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.stream.Stream

@ExtendWith(SpringExtension::class)
@ActiveProfiles(profiles = ["test"])
class UserMapperTest {
    private val userMapper = UserMapper.instance()
    private val faker = Faker()

    @Test
    fun testToUser() {
        val createUserRequest = CreateUserRequest(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true,
        )
        val user = userMapper.toUser(createUserRequest)
        assertThat(user.id).isEqualTo(0L)
        assertThat(user.email).isEqualTo(createUserRequest.email)
        assertThat(user.fullName).isEqualTo(createUserRequest.fullName)
        assertThat(user.active).isEqualTo(createUserRequest.active)
        assertThat(user.createdAt).isNull()
        assertThat(user.updatedAt).isNull()
    }

    @Test
    fun testToUserResponse() {
        val user = User(
            email = faker.internet().emailAddress(),
            fullName = faker.name().fullName(),
            address = faker.address().fullAddress(),
            active = true,
        )
        val (id, email, fullName, _, active) = userMapper.toUserResponse(user)
        assertThat(id).isEqualTo(0L)
        assertThat(email).isEqualTo(user.email)
        assertThat(fullName).isEqualTo(user.fullName)
        assertThat(active).isEqualTo(user.active)
        assertThat(user.createdAt).isNull()
        assertThat(user.updatedAt).isNull()
    }

    @ParameterizedTest
    @MethodSource("provideUpdateUserFromRequest")
    fun testUpdateUserFromRequest(newFullName: String?, newActive: Boolean?, expectedUser: User) {
        val user = User(
            email = "email@test",
            fullName = "fullName",
            address = "address",
            active = true,
        )
        val updateUserRequest = UpdateUserRequest(
            fullName = newFullName,
            active = newActive,
        )
        userMapper.updateUserFromRequest(updateUserRequest, user)
        assertThat(user.email).isEqualTo(expectedUser.email)
    }

    companion object {
        @JvmStatic
        private fun provideUpdateUserFromRequest(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "fullName2",
                    false,
                    User(email = "email@test", fullName = "fullName2", address = "address", active = false),
                ),
                Arguments.of(null, null, User(email = "email@test", fullName = "fullName", address = "address", active = true)),
                Arguments.of(
                    "fullName2",
                    null,
                    User(email = "email@test", fullName = "fullName2", address = "address", active = true),
                ),
                Arguments.of(null, false, User(email = "email@test", fullName = "fullName", address = "address", active = false)),
                Arguments.of(
                    null,
                    null,
                    User(
                        email = "email@test",
                        fullName = "fullName",
                        address = "address",
                        active = true,
                    ),
                ),
            )
        }
    }
}
