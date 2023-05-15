package com.github.manosbatsis.services.event.service

import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.repository.UserEventRepository
import org.springframework.stereotype.Service

@Service
class UserEventServiceImpl(
    val userEventRepository: UserEventRepository,
) : UserEventService {
    override fun findByUserId(id: Long): List<UserEvent> {
        return userEventRepository.findByKeyUserId(id)
    }

    override fun findAll(): List<UserEvent> {
        return userEventRepository.findAll()
    }

    override fun save(userEvent: UserEvent): UserEvent {
        return userEventRepository.save(userEvent)
    }
}
