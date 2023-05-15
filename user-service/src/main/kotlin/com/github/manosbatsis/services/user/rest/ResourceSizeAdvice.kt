package com.github.manosbatsis.services.user.rest

import com.github.manosbatsis.lib.core.rest.ResourceSizeAdvice
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice(annotations = [RestController::class])
class ResourceSizeAdvice : ResourceSizeAdvice()
