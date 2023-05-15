package com.github.manosbatsis.services.event.model

import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import java.util.Date

@PrimaryKeyClass
data class UserEventKey(
    @field:PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    val userId: Long,
    @field:PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    val datetime: Date,
) {
    fun asString() = userId + datetime.time
}
