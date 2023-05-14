package com.github.manosbatsis.services.event.service;

import com.github.manosbatsis.services.event.model.UserEvent;
import com.github.manosbatsis.services.event.repository.UserEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserEventServiceImpl implements UserEventService {

    private final UserEventRepository userEventRepository;

    @Override
    public List<UserEvent> getUserEvents(Long id) {
        List<UserEvent> userEvents = userEventRepository.findByKeyUserId(id);
        return userEvents;
    }

    @Override
    public UserEvent saveUserEvent(UserEvent userEvent) {
        return userEventRepository.save(userEvent);
    }
}
