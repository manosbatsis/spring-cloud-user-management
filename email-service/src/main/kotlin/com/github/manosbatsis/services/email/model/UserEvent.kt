package com.github.manosbatsis.services.email.model

data class UserEvent(
    val key: UserEventKey,
    val type: String,
    val data: String,
)
