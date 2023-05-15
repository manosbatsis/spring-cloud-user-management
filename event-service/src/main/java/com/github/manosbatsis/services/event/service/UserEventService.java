package com.github.manosbatsis.services.event.service;

import com.github.manosbatsis.services.event.model.UserEvent;

import java.util.List;

public interface UserEventService {

    List<UserEvent> findByUserId(Long id);

    List<UserEvent> findAll();

    UserEvent save(UserEvent userEvent);
}
