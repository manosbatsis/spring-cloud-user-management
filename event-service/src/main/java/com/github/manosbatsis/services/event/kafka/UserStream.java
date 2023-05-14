package com.github.manosbatsis.services.event.kafka;

import com.github.manosbatsis.services.event.mapper.UserMapper;
import com.github.manosbatsis.services.event.model.UserEvent;
import com.github.manosbatsis.services.event.service.UserEventService;
import com.github.manosbatsis.services.user.messages.UserEventMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

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
