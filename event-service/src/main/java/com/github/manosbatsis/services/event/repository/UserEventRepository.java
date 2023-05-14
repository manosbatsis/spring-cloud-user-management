package com.github.manosbatsis.services.event.repository;

import com.github.manosbatsis.services.event.model.UserEvent;
import com.github.manosbatsis.services.event.model.UserEventKey;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserEventRepository extends CassandraRepository<UserEvent, UserEventKey> {

    List<UserEvent> findByKeyUserId(Long id);
}
