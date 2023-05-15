package com.github.manosbatsis.services.user.rest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class ResourceSizeAdvice extends com.github.manosbatsis.lib.core.rest.ResourceSizeAdvice {}
