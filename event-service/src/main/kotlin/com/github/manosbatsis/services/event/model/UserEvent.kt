package com.github.manosbatsis.services.event.model

import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("user_events")
data class UserEvent(
    @PrimaryKey val key: UserEventKey,
    val type: String,
    val data: String,
)
