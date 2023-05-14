package com.github.manosbatsis.services.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Slf4j
@Testcontainers
public abstract class AbstractTestcontainers {

    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:5.7.41");
    private static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.1"));
    private static final GenericContainer<?> schemaRegistryContainer =
            new GenericContainer<>("confluentinc/cp-schema-registry:7.3.1");
    private static final CassandraContainer<?> cassandraContainer =
            new CassandraContainer<>("cassandra:4.1.1");
    private static final GenericContainer<?> discoveryServiceContainer =
            new GenericContainer<>("manosbatsis/discovery-service:1.0.0");
    private static final GenericContainer<?> configServiceContainer =
            new GenericContainer<>("manosbatsis/config-service:1.0.0");
    private static final GenericContainer<?> eventServiceContainer =
            new GenericContainer<>("manosbatsis/event-service:1.0.0");
    private static final GenericContainer<?> emailServiceContainer =
            new GenericContainer<>("manosbatsis/email-service:1.0.0");
    private static final GenericContainer greenMailContainer =
            new GenericContainer<>(DockerImageName.parse("greenmail/standalone:latest"));

    protected static String EVENT_SERVICE_API_URL;
    private static final int DISCOVERY_SERVICE_EXPOSED_PORT = 8761;
    private static final int CONFIG_SERVICE_EXPOSED_PORT = 8088;
    private static final int EVENT_SERVICE_EXPOSED_PORT = 9081;
    private static final int EMAIL_SERVICE_EXPOSED_PORT = 9082;

    public static final Duration STARTUP_TIMEOUT = Duration.ofMinutes(2);

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {
        Network network = Network.SHARED;

        // MySQL
        mySQLContainer
                .withNetwork(network)
                .withNetworkAliases("mysql")
                .withUrlParam("characterEncoding", "UTF-8")
                .withUrlParam("serverTimezone", "UTC")
                .start();

        // Kafka
        kafkaContainer
                .withNetwork(network)
                .withNetworkAliases("kafka")
                .withExposedPorts(9092, 9093)
                .start();

        // Schema Registry
        schemaRegistryContainer
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "kafka:9092")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withExposedPorts(8081)
                .waitingFor(Wait.forListeningPort().withStartupTimeout(STARTUP_TIMEOUT))
                .start();

        // Cassandra
        cassandraContainer.withNetwork(network).withNetworkAliases("cassandra").start();

        greenMailContainer
                .withNetwork(network)
                .withNetworkAliases("greenmail")
                .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
                .withEnv(
                        "GREENMAIL_OPTS",
                        "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=user:password")
                .withExposedPorts(3025)
                .start();

        // config-service
        configServiceContainer
                .withNetwork(network)
                .withNetworkAliases("config-service")
                .withExposedPorts(CONFIG_SERVICE_EXPOSED_PORT)
                .waitingFor(
                        Wait.forHttp("/user/default")
                                .forPort(CONFIG_SERVICE_EXPOSED_PORT)
                                .forStatusCode(200)
                                .withStartupTimeout(STARTUP_TIMEOUT))
                .start();

        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        // discovery-service
        discoveryServiceContainer
                .withNetwork(network)
                .withNetworkAliases("discovery-service")
                .withEnv("SPRING_PROFILES_ACTIVE", "docker")
                .withExposedPorts(DISCOVERY_SERVICE_EXPOSED_PORT)
                .dependsOn(configServiceContainer)
                .waitingFor(
                        Wait.forHttp("/eureka/v2/apps")
                                .forPort(DISCOVERY_SERVICE_EXPOSED_PORT)
                                .forStatusCode(200)
                                .withStartupTimeout(STARTUP_TIMEOUT))
                .start();

        // email-service
        emailServiceContainer
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
                        greenMailContainer)
                .waitingFor(
                        Wait.forHttp("/actuator/health")
                                .forPort(EMAIL_SERVICE_EXPOSED_PORT)
                                .forStatusCode(200)
                                .withStartupTimeout(STARTUP_TIMEOUT))
                //            .withLogConsumer(new Consumer<OutputFrame>() {
                //                @Override
                //                public void accept(OutputFrame outputFrame) {
                //                    System.err.println("EMAIL-SERVICE: " +
                // outputFrame.getUtf8StringWithoutLineEnding());
                //                }
                //            })
                .start();

        // event-service
        eventServiceContainer
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
                                .withStartupTimeout(STARTUP_TIMEOUT))
                //            .withLogConsumer(new Consumer<OutputFrame>() {
                //                @Override
                //                public void accept(OutputFrame outputFrame) {
                //                    System.err.println("EVENT-SERVICE: " +
                // outputFrame.getUtf8StringWithoutLineEnding());
                //                }
                //            })
                .start();

        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect.storage_engine", () -> "innodb");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add(
                "spring.cloud.stream.kafka.binder.brokers", kafkaContainer::getBootstrapServers);
        registry.add(
                "spring.cloud.schema-registry-client.endpoint",
                () -> "http://localhost:" + schemaRegistryContainer.getMappedPort(8081));
        registry.add(
                "eureka.client.serviceUrl.defaultZone",
                () ->
                        "http://localhost:"
                                + discoveryServiceContainer.getMappedPort(
                                        DISCOVERY_SERVICE_EXPOSED_PORT)
                                + "/eureka/");
        // registry.add("spring.mail.host", greenMailContainer::getHost);
        // registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);

        EVENT_SERVICE_API_URL =
                String.format(
                        "http://localhost:%s/api",
                        eventServiceContainer.getMappedPort(EVENT_SERVICE_EXPOSED_PORT));
    }
}
