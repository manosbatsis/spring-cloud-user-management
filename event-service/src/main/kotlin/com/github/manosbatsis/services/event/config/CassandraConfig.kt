package com.github.manosbatsis.services.event.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.cassandra.SessionFactory
import org.springframework.data.cassandra.config.CqlSessionFactoryBean
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator
import org.springframework.data.cassandra.core.cql.session.init.SessionFactoryInitializer

@Configuration
class CassandraConfig {
    @Value("\${spring.data.cassandra.local-datacenter}")
    private lateinit var localDatacenter: String

    @Value("\${spring.data.cassandra.contact-points}")
    private lateinit var contactPoints: String

    @Value("\${spring.data.cassandra.keyspace-name}")
    private lateinit var keyspaceName: String

    @Value("\${spring.data.cassandra.username:@null}")
    private lateinit var username: String

    @Value("\${spring.data.cassandra.password:@null}")
    private lateinit var password: String

    @Bean
    fun session(): CqlSessionFactoryBean {
        val session = CqlSessionFactoryBean()
        session.setContactPoints(contactPoints)
        session.setLocalDatacenter(localDatacenter)
        session.setKeyspaceName(keyspaceName)
        session.setUsername(username)
        session.setPassword(password)
        session.setKeyspaceCreations(keyspaceCreations)
        return session
    }

    @Bean
    fun sessionFactoryInitializer(sessionFactory: SessionFactory): SessionFactoryInitializer {
        val initializer = SessionFactoryInitializer()
        initializer.setSessionFactory(sessionFactory)
        initializer.setKeyspacePopulator(keyspacePopulator())
        return initializer
    }

    val keyspaceCreations: List<CreateKeyspaceSpecification>
        get() {
            val specification = CreateKeyspaceSpecification.createKeyspace(keyspaceName)
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication()
            return listOf(specification)
        }

    protected fun keyspacePopulator(): KeyspacePopulator {
        return ResourceKeyspacePopulator(ClassPathResource("event-service.cql"))
    }
}
