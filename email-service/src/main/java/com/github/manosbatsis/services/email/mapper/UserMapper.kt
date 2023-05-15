package com.github.manosbatsis.services.email.mapper

import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.email.model.UserEvent
import com.github.manosbatsis.services.email.model.UserEventKey
import com.github.manosbatsis.services.user.messages.EventType
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

    fun toUserEventMessage(userEvent: UserEvent): UserEventMessage {
        log.warn("toUserEventMessage, userEvent: {}", userEvent)
        return UserEventMessage(
            userEvent.key.asString(),
            userEvent.key.datetime.time,
            EventType.valueOf(userEvent.type),
            userEvent.key.userId,
            userEvent.data,
        )
    }
}
