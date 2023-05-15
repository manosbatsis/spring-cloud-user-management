package com.github.manosbatsis.services.user.repository

import com.github.javafaker.Faker
import com.github.manosbatsis.services.user.model.User
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository
    private val faker = Faker()

    @Test
    fun testFindUserByEmailWhenThereNone() {
        val userOptional: User? = userRepository.findUserByEmail("email@" + UUID.randomUUID() + ".com")
        Assertions.assertThat(userOptional).isNull()
    }

    @Test
    fun testFindUserByEmailWhenThereIsOne() {
        val user = User(
            email = faker.internet().emailAddress(),
            fullName = faker.name().fullName(),
            address = faker.address().fullAddress(),
            active = true,
        )
        userRepository.save(user)
        val userOptional: User? = userRepository.findUserByEmail(user.email)
        Assertions.assertThat(userOptional).isNotNull()
        Assertions.assertThat(userOptional!!.id).isEqualTo(1)
        Assertions.assertThat(userOptional.email).isEqualTo(user.email)
        Assertions.assertThat(userOptional.fullName).isEqualTo(user.fullName)
        Assertions.assertThat(userOptional.address).isEqualTo(user.address)
        Assertions.assertThat(userOptional.active).isTrue()
        Assertions.assertThat(userOptional.createdAt).isNotNull()
        Assertions.assertThat(userOptional.updatedAt).isNotNull()
    }
}
