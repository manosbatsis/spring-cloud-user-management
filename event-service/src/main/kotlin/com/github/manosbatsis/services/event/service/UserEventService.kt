package com.github.manosbatsis.services.event.service

import com.github.manosbatsis.services.event.model.UserEvent

interface UserEventService {
    fun findByUserId(id: Long): List<UserEvent>
    fun findAll(): List<UserEvent>
    fun save(userEvent: UserEvent): UserEvent
}
