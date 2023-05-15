package com.github.manosbatsis.services.user.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class UserStreamJsonProcessingException(cause: Throwable?) : RuntimeException(cause)
