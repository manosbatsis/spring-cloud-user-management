package com.github.manosbatsis.eventservice.service;

import com.github.manosbatsis.eventservice.model.UserEvent;
import java.util.List;

public interface UserEventService {

  List<UserEvent> getUserEvents(Long id);

  UserEvent saveUserEvent(UserEvent userEvent);
}
