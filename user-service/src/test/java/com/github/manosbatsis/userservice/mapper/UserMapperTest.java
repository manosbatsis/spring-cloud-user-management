package com.github.manosbatsis.userservice.mapper;

import com.github.javafaker.Faker;
import com.github.manosbatsis.userservice.mapper.UserMapper;
import com.github.manosbatsis.userservice.model.User;
import com.github.manosbatsis.userservice.rest.dto.CreateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UpdateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import(UserMapperImpl.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private Faker faker = new Faker();

    @Test
    void testToUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true
        );

        User user = userMapper.toUser(createUserRequest);

        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo(createUserRequest.getEmail());
        assertThat(user.getFullName()).isEqualTo(createUserRequest.getFullName());
        assertThat(user.getActive()).isEqualTo(createUserRequest.getActive());
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    @Test
    void testToUserResponse() {
        User user = new User(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true
        );

        UserResponse userResponse = userMapper.toUserResponse(user);

        assertThat(userResponse.id()).isNull();
        assertThat(userResponse.email()).isEqualTo(user.getEmail());
        assertThat(userResponse.fullName()).isEqualTo(user.getFullName());
        assertThat(userResponse.active()).isEqualTo(user.getActive());
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideUpdateUserFromRequest")
    void testUpdateUserFromRequest(String newFullName, Boolean newActive, User expectedUser) {

        User user = new User("email@test", "fullName", "address", true);

        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
            .fullName(newFullName)
            .active(newActive)
            .build();

        userMapper.updateUserFromRequest(updateUserRequest, user);
        assertThat(user).isEqualTo(expectedUser);
    }

    private static Stream<Arguments> provideUpdateUserFromRequest() {

        return Stream.of(
                Arguments.of("fullName2", false, new User("email@test", "fullName2", "address", false)),
                Arguments.of(null, null, new User("email@test", "fullName", "address", true)),
                Arguments.of("fullName2", null, new User("email@test", "fullName2", "address", true)),
                Arguments.of(null, false, new User("email@test", "fullName", "address", false)),
                Arguments.of(null, null, new User("email@test", "fullName", "address", true))
        );
    }
}
