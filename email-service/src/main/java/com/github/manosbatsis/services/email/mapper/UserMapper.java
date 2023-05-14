package com.github.manosbatsis.services.email.mapper;

import com.github.manosbatsis.services.email.model.UserEvent;
import com.github.manosbatsis.services.email.model.UserEventKey;
import com.github.manosbatsis.services.user.messages.UserEventMessage;

import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.Date;

@Configuration
@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserEvent createUserEvent(Message<UserEventMessage> message) {
        UserEventMessage userEventMessage = message.getPayload();
        UserEvent userEvent = new UserEvent();
        userEvent.setKey(
                new UserEventKey(
                        userEventMessage.getUserId(),
                        new Date(userEventMessage.getEventTimestamp())));
        userEvent.setType(userEventMessage.getEventType().toString());
        CharSequence userJson = userEventMessage.getUserJson();
        if (userJson != null) {
            userEvent.setData(userJson.toString());
        }
        return userEvent;
    }
}
