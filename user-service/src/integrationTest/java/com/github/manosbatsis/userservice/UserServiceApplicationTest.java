package com.github.manosbatsis.userservice;

import com.github.javafaker.Faker;
import com.github.manosbatsis.lib.test.error.ErrorResponse;
import com.github.manosbatsis.userservice.model.User;
import com.github.manosbatsis.userservice.rest.dto.CreateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UpdateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UserResponse;
import com.github.manosbatsis.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserServiceApplicationTest extends AbstractTestcontainers {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    private Faker faker = new Faker();

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testGetUsersWhenThereIsNone() {
        ResponseEntity<UserResponse[]> responseEntity = testRestTemplate.getForEntity(API_USERS_URL, UserResponse[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void testGetUsersWhenThereIsOne() {
        User user = userRepository.save(getDefaultUser());

        ResponseEntity<UserResponse[]> responseEntity = testRestTemplate.getForEntity(API_USERS_URL, UserResponse[].class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).hasSize(1);
        assertThat(responseEntity.getBody()[0].id()).isEqualTo(user.getId());
        assertThat(responseEntity.getBody()[0].email()).isEqualTo(user.getEmail());
        assertThat(responseEntity.getBody()[0].active()).isEqualTo(user.getActive());
    }

    @Test
    void testGetUserWhenNonExistent() {
        Long id = 999L;
        String url = String.format(API_USERS_USER_ID_URL, id);
        ResponseEntity<ErrorResponse> responseEntity = testRestTemplate.getForEntity(url, ErrorResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().message()).isEqualTo("User does not exist.");
    }

    @Test
    void testGetUserWhenExistent() {
        User user = userRepository.save(getDefaultUser());

        String url = String.format(API_USERS_USER_ID_URL, user.getId());
        ResponseEntity<UserResponse> responseEntity = testRestTemplate.getForEntity(url, UserResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(user.getId());
        assertThat(responseEntity.getBody().email()).isEqualTo(user.getEmail());
        assertThat(responseEntity.getBody().fullName()).isEqualTo(user.getFullName());
        assertThat(responseEntity.getBody().active()).isEqualTo(user.getActive());
    }

    @Test
    void testCreateUser() {
        CreateUserRequest createUserRequest = getDefaultCreateUserRequest();
        ResponseEntity<UserResponse> responseEntity = testRestTemplate.postForEntity(API_USERS_URL, createUserRequest, UserResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isPositive();
        assertThat(responseEntity.getBody().email()).isEqualTo(createUserRequest.getEmail());
        assertThat(responseEntity.getBody().fullName()).isEqualTo(createUserRequest.getFullName());
        assertThat(responseEntity.getBody().active()).isEqualTo(createUserRequest.getActive());

        Long userId = responseEntity.getBody().id();
        Optional<User> userFound = userRepository.findById(userId);
        assertThat(userFound).isPresent();

        await().atMost(AT_MOST_DURATION).pollInterval(POLL_INTERVAL_DURATION).untilAsserted(() -> {
            log.info("Waiting for event-service to receive the message and process ...");
            String eventServiceUrl = String.format("%s/events?userId=%s", EVENT_SERVICE_API_URL, userId);
            ResponseEntity<EventServiceUserEventResponse[]> eventServiceResponseEntity =
                testRestTemplate.getForEntity(eventServiceUrl, EventServiceUserEventResponse[].class);
            assertThat(eventServiceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(eventServiceResponseEntity.getBody()).isNotNull();
            assertThat(Arrays.stream(eventServiceResponseEntity.getBody())
                .anyMatch(userEventResponse -> userEventResponse.type().equals("CREATED"))).isTrue();
        });
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.save(getDefaultUser());
        Long userId = user.getId();

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setActive(false);

        HttpEntity<UpdateUserRequest> requestUpdate = new HttpEntity<>(updateUserRequest);
        String url = String.format(API_USERS_USER_ID_URL, userId);
        ResponseEntity<UserResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.PUT, requestUpdate, UserResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(userId);
        assertThat(responseEntity.getBody().email()).isEqualTo(user.getEmail());
        assertThat(responseEntity.getBody().fullName()).isEqualTo(user.getFullName());
        assertThat(responseEntity.getBody().active()).isEqualTo(updateUserRequest.getActive());

        await().atMost(AT_MOST_DURATION).pollInterval(POLL_INTERVAL_DURATION).untilAsserted(() -> {
            log.info("Waiting for event-service to receive the message and process ...");
            String eventServiceUrl = String.format("%s/events?userId=%s", EVENT_SERVICE_API_URL, userId);
            ResponseEntity<EventServiceUserEventResponse[]> eventServiceResponseEntity =
                testRestTemplate.getForEntity(eventServiceUrl, EventServiceUserEventResponse[].class);
            assertThat(eventServiceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(eventServiceResponseEntity.getBody()).isNotNull();
            assertThat(Arrays.stream(eventServiceResponseEntity.getBody())
                .anyMatch(userEventResponse -> userEventResponse.type().equals("UPDATED"))).isTrue();
        });
    }
    @Test
    void testDeleteUser() {
        User user = userRepository.save(getDefaultUser());
        Long userId = user.getId();

        String url = String.format(API_USERS_USER_ID_URL, userId);
        ResponseEntity<UserResponse> responseEntity = testRestTemplate.exchange(url, HttpMethod.DELETE, null, UserResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().id()).isEqualTo(userId);
        assertThat(responseEntity.getBody().email()).isEqualTo(user.getEmail());
        assertThat(responseEntity.getBody().fullName()).isEqualTo(user.getFullName());
        assertThat(responseEntity.getBody().active()).isEqualTo(user.getActive());

        Optional<User> userNotFound = userRepository.findById(userId);
        assertThat(userNotFound).isNotPresent();

        await().atMost(AT_MOST_DURATION).pollInterval(POLL_INTERVAL_DURATION).untilAsserted(() -> {
            log.info("Waiting for event-service to receive the message and process ...");
            String eventServiceUrl = String.format("%s/events?userId=%s", EVENT_SERVICE_API_URL, userId);
            ResponseEntity<EventServiceUserEventResponse[]> eventServiceResponseEntity =
                testRestTemplate.getForEntity(eventServiceUrl, EventServiceUserEventResponse[].class);
            assertThat(eventServiceResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(eventServiceResponseEntity.getBody()).isNotNull();
            assertThat(Arrays.stream(eventServiceResponseEntity.getBody())
                .anyMatch(userEventResponse -> userEventResponse.type().equals("DELETED"))).isTrue();
        });
    }

    private User getDefaultUser() {
        return new User(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true);
    }

    private CreateUserRequest getDefaultCreateUserRequest() {
        return new CreateUserRequest(
            faker.internet().emailAddress(),
            faker.name().fullName(),
            faker.address().fullAddress(),
            true);
    }

    private record EventServiceUserEventResponse(Long userId, String datetime, String type, String data) {
    }

    private static final String API_USERS_URL = "/api/users";
    private static final String API_USERS_USER_ID_URL = "/api/users/%s";

    public static final Duration AT_MOST_DURATION = Duration.ofSeconds(10);
    public static final Duration POLL_INTERVAL_DURATION = Duration.ofSeconds(1);
}
