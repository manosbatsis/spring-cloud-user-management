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
    public List<UserEvent> findByUserId(Long id) {
        return userEventRepository.findByKeyUserId(id);
    }

    @Override
    public List<UserEvent> findAll() {
        return userEventRepository.findAll();
    }

    @Override
    public UserEvent save(UserEvent userEvent) {
        return userEventRepository.save(userEvent);
    }
}
