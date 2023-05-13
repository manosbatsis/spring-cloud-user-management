package com.github.manosbatsis.services.user.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.manosbatsis.services.user.exception.UserStreamJsonProcessingException;
import com.github.manosbatsis.services.user.messages.EventType;
import com.github.manosbatsis.services.user.messages.UserEventMessage;
import com.github.manosbatsis.services.user.model.User;
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest;
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.UUID;

@Slf4j
@Component
public class UserStream {

    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${spring.cloud.stream.bindings.users-out-0.content-type:application/json}")
    private String streamOutMimeType;

    public Message<UserEventMessage> userCreated(Long userId, CreateUserRequest createUserRequest) {
        return sendToBus(userId, EventType.CREATED, writeValueAsString(createUserRequest));
    }

    public Message<UserEventMessage> userCreated(User user) {
        return sendToBus(user.getId(), EventType.CREATED, writeValueAsString(user));
    }

    public Message<UserEventMessage> userUpdated(Long userId, UpdateUserRequest updateUserRequest) {
        return sendToBus(userId, EventType.UPDATED, writeValueAsString(updateUserRequest));
    }

    public Message<UserEventMessage> userUpdated(User user) {
        return sendToBus(user.getId(), EventType.UPDATED, writeValueAsString(user));
    }

    public Message<UserEventMessage> userDeleted(Long userId) {
        return sendToBus(userId, EventType.DELETED, null);
    }

    private Message<UserEventMessage> sendToBus(Long userId, EventType eventType, String json) {
        UserEventMessage userEventMessage = new UserEventMessage(
            UUID.randomUUID().toString(),
            System.currentTimeMillis(),
            eventType,
            userId,
            json
        );

        return sendToBus(userId, userEventMessage);
    }

    private Message<UserEventMessage> sendToBus(Long partitionKey, UserEventMessage userEventMessage) {
        Message<UserEventMessage> message = MessageBuilder.withPayload(userEventMessage)
            .setHeader("partitionKey", partitionKey)
            .setHeader("target-protocol", "kafka")
            .build();

        streamBridge.send("users-topic", message, MimeType.valueOf(streamOutMimeType));
        return message;
    }


    private String writeValueAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new UserStreamJsonProcessingException(e);
        }
    }
}
