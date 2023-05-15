package com.github.manosbatsis.lib.test.error
data class ErrorResponse(
    val code: String,
    val message: String,
    val fieldErrors: List<FieldError>? = null,
)
