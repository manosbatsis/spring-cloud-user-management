package com.github.manosbatsis.eventservice.kafka;

import com.github.manosbatsis.eventservice.mapper.UserMapper;
import com.github.manosbatsis.eventservice.model.UserEvent;
import com.github.manosbatsis.eventservice.service.UserEventService;
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

  @Autowired private UserEventService userEventService;

  @Autowired private UserMapper userMapper;

  @Bean
  public Consumer<Message<UserEventMessage>> users() {
    return message -> {
      try {
        UserEvent userEvent = userMapper.createUserEvent(message);
        userEventService.saveUserEvent(userEvent);
      } catch (Exception e) {
        log.error("An error occurred while saving userEvent {}", message, e);
        e.printStackTrace();
      }
    };
  }
}
