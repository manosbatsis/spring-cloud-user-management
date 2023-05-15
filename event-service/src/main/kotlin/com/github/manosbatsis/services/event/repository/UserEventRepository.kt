package com.github.manosbatsis.services.event.repository

import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEventRepository : CassandraRepository<UserEvent, UserEventKey> {
    fun findByKeyUserId(id: Long): List<UserEvent>
}
