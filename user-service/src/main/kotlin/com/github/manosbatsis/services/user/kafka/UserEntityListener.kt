package com.github.manosbatsis.services.user.kafka

import com.github.manosbatsis.lib.core.log.loggerFor
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.service.SpringContext
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate

/**
 * Listens to JPA Entity Lifecycle Events related to User instances to trigger event streaming using
 * the configured message broker.
 */

class UserEntityListener {
    companion object {
        private val log = loggerFor<UserEntityListener>()
    }

    @PostPersist
    private fun created(user: User) {
        log.info("User created: {}", user)
        userStream.userCreated(user)
    }

    @PostUpdate
    private fun updated(user: User) {
        log.info("User updated: {}", user)
        when (user.deleted) {
            false -> userStream.userUpdated(user)
            true -> userStream.userDeleted(user)
        }
    }

    @PostRemove
    private fun deleted(user: User) {
        log.info("User deleted: {}", user)
        userStream.userDeleted(user)
    }

    private val userStream: UserStream
        get() = SpringContext.getBean(UserStream::class.java)
}
