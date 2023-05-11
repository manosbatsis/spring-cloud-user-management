package com.github.manosbatsis.emailservice.mapper;

import com.github.manosbatsis.emailservice.model.UserEvent;
import com.github.manosbatsis.emailservice.model.UserEventKey;
import com.github.manosbatsis.userservice.messages.UserEventMessage;
import java.util.Date;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@Mapper(componentModel = "spring")
public interface UserMapper {

  default UserEvent createUserEvent(Message<UserEventMessage> message) {
    UserEventMessage userEventMessage = message.getPayload();
    UserEvent userEvent = new UserEvent();
    userEvent.setKey(
        new UserEventKey(
            userEventMessage.getUserId(), new Date(userEventMessage.getEventTimestamp())));
    userEvent.setType(userEventMessage.getEventType().toString());
    CharSequence userJson = userEventMessage.getUserJson();
    if (userJson != null) {
      userEvent.setData(userJson.toString());
    }
    return userEvent;
  }
}
