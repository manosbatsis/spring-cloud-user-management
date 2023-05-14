package com.github.manosbatsis.services.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import com.github.manosbatsis.services.user.model.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    private Faker faker = new Faker();

    @Test
    void testFindUserByEmailWhenThereNone() {
        Optional<User> userOptional =
                userRepository.findUserByEmail("email@" + UUID.randomUUID() + ".com");

        assertThat(userOptional).isNotNull();
        assertThat(userOptional.isPresent()).isFalse();
    }

    @Test
    void testFindUserByEmailWhenThereIsOne() {
        User user =
                new User(
                        faker.internet().emailAddress(),
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        true);
        userRepository.save(user);

        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        assertThat(userOptional).isNotNull();
        assertThat(userOptional.isPresent()).isTrue();
        assertThat(userOptional.get().getId()).isEqualTo(1);
        assertThat(userOptional.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(userOptional.get().getFullName()).isEqualTo(user.getFullName());
        assertThat(userOptional.get().getAddress()).isEqualTo(user.getAddress());
        assertThat(userOptional.get().getActive()).isTrue();
        assertThat(userOptional.get().getCreatedAt()).isNotNull();
        assertThat(userOptional.get().getUpdatedAt()).isNotNull();
    }
}
