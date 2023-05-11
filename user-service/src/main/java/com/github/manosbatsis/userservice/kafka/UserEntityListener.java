package com.github.manosbatsis.userservice.kafka;

import com.github.manosbatsis.userservice.mapper.UserMapper;
import com.github.manosbatsis.userservice.model.User;
import com.github.manosbatsis.userservice.service.SpringContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
