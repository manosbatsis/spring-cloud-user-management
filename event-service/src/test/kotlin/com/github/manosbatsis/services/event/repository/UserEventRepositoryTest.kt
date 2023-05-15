package com.github.manosbatsis.services.event.repository

import com.github.manosbatsis.services.event.model.UserEvent
import com.github.manosbatsis.services.event.model.UserEventKey
import org.assertj.core.api.Assertions
import org.cassandraunit.spring.CassandraDataSet
import org.cassandraunit.spring.CassandraUnitDependencyInjectionTestExecutionListener
import org.cassandraunit.spring.EmbeddedCassandra
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import java.util.Date

@Disabled
@TestExecutionListeners(
    CassandraUnitDependencyInjectionTestExecutionListener::class,
    DependencyInjectionTestExecutionListener::class,
)
@EmbeddedCassandra(timeout = 60000)
@CassandraDataSet(value = ["event-service.cql"], keyspace = "manosbatsis")
@DataCassandraTest
@TestPropertySource(properties = ["spring.data.cassandra.contact-points=localhost:9142", "spring.data.cassandra.schema-action=RECREATE"])
class UserEventRepositoryTest {
    @Autowired
    private lateinit var userEventRepository: UserEventRepository

    @Test
    fun testFindByKeyUserIdWhenThereIsNone() {
        val userEvents = userEventRepository.findByKeyUserId(1L)
        Assertions.assertThat(userEvents).isEmpty()
    }

    @Test
    fun testFindByKeyUserIdWhenThereIsOne() {
        val userEvent = createUserEvent(1L, Date(), "type", "data")
        userEventRepository.save(userEvent)
        val userEvents = userEventRepository.findByKeyUserId(1L)
        Assertions.assertThat(userEvents).hasSize(1)
        Assertions.assertThat(userEvents[0]).isEqualTo(userEvent)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testFindByKeyUserIdWhenThereAreTwo() {
        val userEvent1 = createUserEvent(1L, Date(), "type1", "data1")
        userEventRepository.save(userEvent1)
        Thread.sleep(2000)
        val userEvent2 = createUserEvent(1L, Date(), "type2", "data2")
        userEventRepository.save(userEvent2)
        val userEvents = userEventRepository.findByKeyUserId(1L)
        Assertions.assertThat(userEvents).hasSize(2)
        Assertions.assertThat(userEvents[0]).isEqualTo(userEvent1)
        Assertions.assertThat(userEvents[1]).isEqualTo(userEvent2)
    }

    private fun createUserEvent(userId: Long, datetime: Date, type: String, data: String): UserEvent {
        return UserEvent(UserEventKey(userId, datetime), type, data)
    }
}
