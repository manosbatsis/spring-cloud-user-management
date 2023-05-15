package com.github.manosbatsis.services.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.CONFLICT)
class UserEmailDuplicatedException(message: String) : RuntimeException(message)
