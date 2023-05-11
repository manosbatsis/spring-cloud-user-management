package com.github.manosbatsis.eventservice.repository;

import com.github.manosbatsis.eventservice.model.UserEvent;
import com.github.manosbatsis.eventservice.model.UserEventKey;
import java.util.List;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEventRepository extends CassandraRepository<UserEvent, UserEventKey> {

  List<UserEvent> findByKeyUserId(Long id);
}
