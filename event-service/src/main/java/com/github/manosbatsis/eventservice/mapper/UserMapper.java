package com.github.manosbatsis.eventservice.mapper;

import com.github.manosbatsis.eventservice.model.UserEvent;
import com.github.manosbatsis.eventservice.model.UserEventKey;
import com.github.manosbatsis.eventservice.rest.dto.UserEventResponse;
import com.github.manosbatsis.userservice.messages.UserEventMessage;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

@Configuration
@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(source = "key.userId", target = "userId")
  @Mapping(source = "key.datetime", target = "datetime")
  UserEventResponse toUserEventResponse(UserEvent userEvent);

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
