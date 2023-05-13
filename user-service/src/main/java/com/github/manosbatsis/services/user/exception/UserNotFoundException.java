package com.github.manosbatsis.services.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public static final String DEFAULT_MSG = "User does not exist.";

    public UserNotFoundException() {
        super(DEFAULT_MSG);
    }

    public UserNotFoundException(Long userId) {
        super(String.format("User with id '%d' doesn't exist.", userId));
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
