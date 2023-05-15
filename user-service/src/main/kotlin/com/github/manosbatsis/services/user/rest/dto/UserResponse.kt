package com.github.manosbatsis.services.user.rest.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val address: String,
    val active: Boolean,
)
