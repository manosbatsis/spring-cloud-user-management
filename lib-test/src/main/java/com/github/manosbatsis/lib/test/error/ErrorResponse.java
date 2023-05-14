package com.github.manosbatsis.lib.test.error;

import java.util.Collections;
import java.util.List;

public record ErrorResponse(String code, String message, List<FieldError> fieldErrors) {
    public ErrorResponse(String code, String message) {
        this(code, message, Collections.emptyList());
    }
}
