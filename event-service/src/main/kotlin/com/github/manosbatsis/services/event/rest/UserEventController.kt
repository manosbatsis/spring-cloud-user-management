package com.github.manosbatsis.services.event.rest

import com.github.manosbatsis.services.event.mapper.UserMapper
import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.rest.dto.UserEventResponse
import com.github.manosbatsis.services.event.service.UserEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/events")
class UserEventController(
    private val userEventService: UserEventService,
) {

    private val userMapper = UserMapper

    @GetMapping
    fun getUserEvents(@RequestParam(name = "userId") userId: Long): List<UserEventResponse> {
        return userEventService.findByUserId(userId).stream()
            .map { userEvent: UserEvent -> userMapper.toUserEventResponse(userEvent) }
            .collect(Collectors.toList())
    }

    @get:GetMapping("all")
    val userEvents: List<UserEventResponse>
        get() = userEventService.findAll().stream()
            .map { userEvent: UserEvent -> userMapper.toUserEventResponse(userEvent) }
            .collect(Collectors.toList())
}
