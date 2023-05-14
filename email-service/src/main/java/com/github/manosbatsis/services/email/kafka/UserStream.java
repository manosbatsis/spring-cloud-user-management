package com.github.manosbatsis.services.email.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manosbatsis.services.email.mapper.UserMapper;
import com.github.manosbatsis.services.email.model.EmailRecepient;
import com.github.manosbatsis.services.email.model.UserEvent;
import com.github.manosbatsis.services.email.service.EmailService;
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
