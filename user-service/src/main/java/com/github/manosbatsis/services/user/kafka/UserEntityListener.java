package com.github.manosbatsis.services.user.kafka;

import com.github.manosbatsis.services.user.model.User;
import com.github.manosbatsis.services.user.service.SpringContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Listens to JPA Entity Lifecycle Events related to User instances
 * to trigger event streaming using the configured message broker.
 */
@Slf4j
public class UserEntityListener {

    @PostPersist
    private void created(User user) {
        log.debug("User created: {}", user);
        getUserStream().userCreated(user);
    }

    @PostUpdate
    private void updated(User user) {
        log.debug("User updated: {}", user);
        getUserStream().userUpdated(user);
    }

    @PostRemove
    private void deleted(User user) {
        log.debug("User deleted: {}", user);
        getUserStream().userDeleted(user.getId());
    }

    private UserStream getUserStream() {
        return SpringContext.getBean(UserStream.class);
    }
}
