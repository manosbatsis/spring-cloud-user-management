package com.github.manosbatsis.emailservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manosbatsis.emailservice.mapper.UserMapper;
import com.github.manosbatsis.emailservice.model.EmailRecepient;
import com.github.manosbatsis.emailservice.model.UserEvent;
import com.github.manosbatsis.emailservice.service.EmailService;
import com.github.manosbatsis.userservice.messages.UserEventMessage;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Slf4j
@Configuration
public class UserStream {

  @Autowired private UserMapper userMapper;
  @Autowired private EmailService emailService;

  @Autowired private ObjectMapper objectMapper;

  @Bean
  public Consumer<Message<UserEventMessage>> users() {
    return message -> {
      UserEvent userEvent = userMapper.createUserEvent(message);
      if (userEvent.getType().equals("CREATED")) {
        EmailRecepient emailRecepient = getEmailRecepient(userEvent);
        emailService.sendRegistrationConfirmation(emailRecepient);
      } else {
        log.debug("Ignored user event: \n{}\n", userEvent.getType());
      }
    };
  }

  private EmailRecepient getEmailRecepient(UserEvent userEvent) {
    try {
      return objectMapper.readValue(userEvent.getData(), EmailRecepient.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
