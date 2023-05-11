package com.github.manosbatsis.emailservice.kafka;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javafaker.Faker;
import com.github.manosbatsis.lib.test.Constants;
import com.github.manosbatsis.userservice.messages.EventType;
import com.github.manosbatsis.userservice.messages.UserEventMessage;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

/** This class has the same test as {@link UserStreamTest} using a different style */
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class UserStreamTest {

  public static final Duration AT_MOST_DURATION = Duration.ofSeconds(10);

  @Autowired private InputDestination inputDestination;

  private Faker faker = new Faker();

  @RegisterExtension
  static GreenMailExtension greenMail =
      new GreenMailExtension(ServerSetupTest.SMTP)
          .withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"))
          .withPerMethodLifecycle(false);

  @Test
  void testEmail() {
    String eventId = UUID.randomUUID().toString();
    Date datetime = new Date();
    EventType eventType = EventType.CREATED;
    Long userId = 1L;
    String expectedEmailAddress = faker.internet().emailAddress();
    String userJson =
        "{\"email\":\""
            + expectedEmailAddress
            + "\",\"fullName\":\"fullName\",\"address\":\"address\",\"active\":true}";

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

    await()
        .atMost(AT_MOST_DURATION)
        .untilAsserted(
            () -> {
              MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
              assertEquals(1, receivedMessages.length);

              MimeMessage receivedMessage = receivedMessages[0];
              assertTrue(GreenMailUtil.getBody(receivedMessage).contains("Your new account"));
              assertEquals(1, receivedMessage.getAllRecipients().length);
              assertEquals(expectedEmailAddress, receivedMessage.getAllRecipients()[0].toString());
            });
  }
}
