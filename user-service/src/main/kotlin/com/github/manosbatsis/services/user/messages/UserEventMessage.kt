package com.github.manosbatsis.services.user.messages

data class UserEventMessage(
    // It does not work if we change this class to Java Record
    var eventId: String,
    var eventTimestamp: Long,
    var eventType: EventType,
    var userId: Long,
    var userJson: String?,
)
