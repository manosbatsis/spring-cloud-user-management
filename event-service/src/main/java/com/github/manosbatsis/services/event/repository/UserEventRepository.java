package com.github.manosbatsis.services.event.repository;

import com.github.manosbatsis.services.event.model.UserEvent;
import com.github.manosbatsis.services.event.model.UserEventKey;
import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventRepository extends CassandraRepository<UserEvent, UserEventKey> {

  List<UserEvent> findByKeyUserId(Long id);
}
