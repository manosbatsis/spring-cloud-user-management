package com.github.manosbatsis.services.event.mapper

import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import com.github.manosbatsis.services.event.rest.dto.UserEventResponse
import com.github.manosbatsis.services.user.messages.UserEventMessage
import java.util.Date

object UserMapper {
    private val log = loggerFor<UserMapper>()
    fun toUserEvent(userEventMessage: UserEventMessage): UserEvent {
        log.warn("toUserEvent, userEventMessage: {}", userEventMessage)
        return UserEvent(
            key = UserEventKey(
                userEventMessage.getUserId(),
                Date(userEventMessage.getEventTimestamp()),
            ),
            type = userEventMessage.getEventType().toString(),
            data = userEventMessage.getUserJson(),
        )
    }

    fun toUserEventResponse(userEvent: UserEvent): UserEventResponse {
        log.warn("toUserEventMessage, userEvent: {}", userEvent)
        return UserEventResponse(
            userEvent.key.userId,
            userEvent.key.datetime,
            userEvent.type,
            userEvent.data,
        )
    }
}
