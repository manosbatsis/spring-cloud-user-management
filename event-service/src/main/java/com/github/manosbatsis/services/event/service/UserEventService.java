package com.github.manosbatsis.services.event.service;

import com.github.manosbatsis.services.event.model.UserEvent;

import java.util.List;

public interface UserEventService {

    List<UserEvent> getUserEvents(Long id);

    UserEvent saveUserEvent(UserEvent userEvent);
}
