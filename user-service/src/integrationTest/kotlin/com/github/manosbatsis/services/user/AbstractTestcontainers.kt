package com.github.manosbatsis.services.user

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.*
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@Testcontainers
abstract class AbstractTestcontainers {
    companion object {
        @JvmStatic
        protected lateinit var EVENT_SERVICE_API_URL: String
        private const val DISCOVERY_SERVICE_EXPOSED_PORT = 8761
        private const val CONFIG_SERVICE_EXPOSED_PORT = 8088
        private const val EVENT_SERVICE_EXPOSED_PORT = 9081
        private const val EMAIL_SERVICE_EXPOSED_PORT = 9082
        val STARTUP_TIMEOUT = Duration.ofMinutes(2)

        @JvmStatic
        val network = Network.SHARED

        @Container @JvmStatic
        val mySQLContainer: MySQLContainer<*> = MySQLContainer("mysql:5.7.41")
            .withNetwork(network)
            .withNetworkAliases("mysql")
            .withUrlParam("characterEncoding", "UTF-8")
            .withUrlParam("serverTimezone", "UTC")

        @Container @JvmStatic
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"))
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withExposedPorts(9092, 9093)

        @Container @JvmStatic
        val schemaRegistryContainer: GenericContainer<*> =
            GenericContainer("confluentinc/cp-schema-registry:7.3.1")
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withExposedPorts(8081)
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT))

        @Container @JvmStatic
        val cassandraContainer: CassandraContainer<*> = CassandraContainer("cassandra:4.1.1")
            .withNetwork(network).withNetworkAliases("cassandra")

        @Container @JvmStatic
        val configServiceContainer: GenericContainer<*> =
            GenericContainer("manosbatsis/config-service:1.0.0")
                .withNetwork(network)
                .withNetworkAliases("config-service")
                .withExposedPorts(CONFIG_SERVICE_EXPOSED_PORT)
                .waitingFor(
                    Wait.forHttp("/user/default")
                        .forPort(CONFIG_SERVICE_EXPOSED_PORT)
                        .forStatusCode(200)
                        .withStartupTimeout(STARTUP_TIMEOUT),
                )
                .withLogConsumer {
                    @Override
                    fun accept(outputFrame: OutputFrame) {
                        System.err.println("CONFIG-SERVICE: " + outputFrame.utf8StringWithoutLineEnding)
                    }
                }

        @Container @JvmStatic
        val discoveryServiceContainer: GenericContainer<*> =
            GenericContainer("manosbatsis/discovery-service:1.0.0")
                .withNetwork(network)
                .withNetworkAliases("discovery-service")
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withExposedPorts(DISCOVERY_SERVICE_EXPOSED_PORT)
                .dependsOn(configServiceContainer)
                .waitingFor(
                    Wait.forHttp("/eureka/v2/apps")
                        .forPort(DISCOVERY_SERVICE_EXPOSED_PORT)
                        .forStatusCode(200)
                        .withStartupTimeout(STARTUP_TIMEOUT),
                )

        @Container @JvmStatic
        val eventServiceContainer: GenericContainer<*> =
            GenericContainer("manosbatsis/event-service:1.0.0")
                .withNetwork(network)
                .withNetworkAliases("event-service")
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withEnv("KAFKA_HOST", "kafka")
                .withEnv("KAFKA_PORT", "9092")
                .withEnv("SCHEMA_REGISTRY_HOST", "schema-registry")
                .withEnv("CASSANDRA_HOST", "cassandra")
                .withEnv("MANAGEMENT_TRACING_ENABLED", "false")
                .withExposedPorts(EVENT_SERVICE_EXPOSED_PORT)
                .dependsOn(schemaRegistryContainer, kafkaContainer, cassandraContainer)
                .waitingFor(
                    Wait.forHttp("/actuator/health")
                        .forPort(EVENT_SERVICE_EXPOSED_PORT)
                        .forStatusCode(200)
                        .withStartupTimeout(STARTUP_TIMEOUT),
                )

        @Container @JvmStatic
        val greenMailContainer: GenericContainer<*> =
            GenericContainer(DockerImageName.parse("greenmail/standalone:latest"))
                .withNetwork(network)
                .withNetworkAliases("greenmail")
                .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
                .withEnv(
                    "GREENMAIL_OPTS",
                    "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=user:password",
                )
                .withExposedPorts(3025)

        @Container @JvmStatic
        val emailServiceContainer: GenericContainer<*> =
            GenericContainer("manosbatsis/email-service:1.0.0")
                .withNetwork(network)
                .withNetworkAliases("email-service")
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withEnv("KAFKA_HOST", "kafka")
                .withEnv("KAFKA_PORT", "9092")
                .withEnv("EMAIL_HOST", "greenmail")
                .withEnv("EMAIL_PORT", "3025")
                .withEnv("EMAIL_USER", "user")
                .withEnv("EMAIL_PASSWORD", "password")
                .withEnv("SCHEMA_REGISTRY_HOST", "schema-registry")
                .withEnv("MANAGEMENT_TRACING_ENABLED", "false")
                .withExposedPorts(EMAIL_SERVICE_EXPOSED_PORT)
                .dependsOn(
                    configServiceContainer,
                    schemaRegistryContainer,
                    kafkaContainer,
                    greenMailContainer,
                )
                .waitingFor(
                    Wait.forHttp("/actuator/health")
                        .forPort(EMAIL_SERVICE_EXPOSED_PORT)
                        .forStatusCode(200)
                        .withStartupTimeout(STARTUP_TIMEOUT),
                ) //            .withLogConsumer(new Consumer<OutputFrame>() {
        //                @Override
        //                public void accept(OutputFrame outputFrame) {
        //                    System.err.println("EMAIL-SERVICE: " +
        // outputFrame.getUtf8StringWithoutLineEnding());
        //                }
        //            })

        @JvmStatic
        @DynamicPropertySource
        fun dynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.config.import") {
                "optional:configserver:http://localhost:${configServiceContainer.getMappedPort(CONFIG_SERVICE_EXPOSED_PORT)}"
            }
            registry.add("spring.datasource.url") { mySQLContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mySQLContainer.username }
            registry.add("spring.datasource.password") { mySQLContainer.password }
            registry.add("spring.jpa.properties.hibernate.dialect.storage_engine") { "innodb" }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add(
                "spring.cloud.stream.kafka.binder.brokers",
            ) { kafkaContainer.bootstrapServers }
            registry.add(
                "spring.cloud.schema-registry-client.endpoint",
            ) { "http://localhost:" + schemaRegistryContainer.getMappedPort(8081) }
            registry.add(
                "eureka.client.serviceUrl.defaultZone",
            ) {
                (
                    "http://localhost:" +
                        discoveryServiceContainer.getMappedPort(
                            DISCOVERY_SERVICE_EXPOSED_PORT,
                        ) +
                        "/eureka/"
                    )
            }
            // registry.add("spring.mail.host", greenMailContainer::getHost);
            // registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);
            EVENT_SERVICE_API_URL = String.format(
                "http://localhost:%s/api",
                eventServiceContainer.getMappedPort(EVENT_SERVICE_EXPOSED_PORT),
            )
        }
    }

    /*
    init {
        listOf(
            mySQLContainer, kafkaContainer, schemaRegistryContainer, cassandraContainer,
            configServiceContainer, discoveryServiceContainer, eventServiceContainer, greenMailContainer,
            emailServiceContainer,
        ).forEach {
            it.start()
        }
    }
     */
}
