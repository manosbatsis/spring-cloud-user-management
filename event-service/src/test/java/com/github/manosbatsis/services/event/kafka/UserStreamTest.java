package com.github.manosbatsis.services.event.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.manosbatsis.lib.test.Constants;
import com.github.manosbatsis.services.event.model.UserEvent;
import com.github.manosbatsis.services.event.repository.UserEventRepository;
import com.github.manosbatsis.services.user.messages.EventType;
import com.github.manosbatsis.services.user.messages.UserEventMessage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@ActiveProfiles({"test"})
@Testcontainers
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class UserStreamTest {

    @Autowired private InputDestination inputDestination;

    @Autowired private UserEventRepository userEventRepository;

    @Container
    private static final CassandraContainer<?> cassandraContainer =
            new CassandraContainer<>("cassandra:4.1.0");

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        String contractPoints =
                String.format(
                        "%s:%s",
                        cassandraContainer.getHost(), cassandraContainer.getMappedPort(9042));
        registry.add("spring.data.cassandra.contact-points", () -> contractPoints);
    }

    @Test
    void testUsers() {
        String eventId = UUID.randomUUID().toString();
        Date datetime = new Date();
        EventType eventType = EventType.CREATED;
        Long userId = 1L;
        String userJson =
                "{\"email\":\"email\",\"fullName\":\"fullName\",\"address\":\"address\",\"active\":true}";

        UserEventMessage userEventMessage =
                UserEventMessage.newBuilder()
                        .setEventId(eventId)
                        .setEventTimestamp(datetime.getTime())
                        .setEventType(eventType)
                        .setUserId(userId)
                        .setUserJson(userJson)
                        .build();

        inputDestination.send(
                MessageBuilder.withPayload(userEventMessage).build(), Constants.TOPIC_NAME_USERS);
        List<UserEvent> userEvents = userEventRepository.findByKeyUserId(userId);

        assertThat(userEvents).isNotNull();
        assertThat(userEvents.size()).isEqualTo(1);
        assertThat(userEvents.get(0).getKey().getUserId()).isEqualTo(userId);
        assertThat(userEvents.get(0).getKey().getDatetime()).isEqualTo(datetime);
        assertThat(userEvents.get(0).getData()).isEqualTo(userJson);
        assertThat(userEvents.get(0).getType()).isEqualTo(eventType.name());
    }
}
