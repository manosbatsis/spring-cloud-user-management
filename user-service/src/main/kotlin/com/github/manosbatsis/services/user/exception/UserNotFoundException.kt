package com.github.manosbatsis.services.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException : RuntimeException {
    constructor() : super(DEFAULT_MSG)
    constructor(userId: Long) : super(String.format("User with id '%d' doesn't exist.", userId))
    constructor(message: String) : super(message)

    companion object {
        const val DEFAULT_MSG = "User does not exist."
    }
}
