package com.github.manosbatsis.lib.test.error

data class FieldError(val code: String, val property: String, val rejectedValue: String, val path: String)
