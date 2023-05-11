package com.github.manosbatsis.eventservice.rest;

import com.github.manosbatsis.eventservice.mapper.UserMapper;
import com.github.manosbatsis.eventservice.rest.dto.UserEventResponse;
import com.github.manosbatsis.eventservice.service.UserEventService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class UserEventController {

  private final UserEventService userEventService;
  private final UserMapper userMapper;

  @GetMapping
  public List<UserEventResponse> getUserEvents(@RequestParam(name = "userId") Long id) {
    return userEventService.getUserEvents(id).stream()
        .map(userMapper::toUserEventResponse)
        .collect(Collectors.toList());
  }
}
