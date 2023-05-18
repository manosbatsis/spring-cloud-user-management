[![CI](https://github.com/manosbatsis/spring-cloud-user-management-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/manosbatsis/spring-cloud-user-management-java/actions/workflows/gradle.yml)
<h1>Spring Cloud User Management</h1>

<!-- TOC -->
* [Overview](#overview)
	* [Prerequisites](#prerequisites)
	* [Modules](#modules)
* [Build HowTo](#build-howto)
* [Test HowTo](#test-howto)
	* [Launch Containers](#launch-containers)
	* [Manual UI Test](#manual-ui-test)
	* [Manual API Test](#manual-api-test)
* [Reference](#reference)
	* [Links](#links)
	* [user-service](#user-service)
	* [event-service](#event-service)
	* [email-service](#email-service)
	* [Useful Commands](#useful-commands)
	* [MySQL](#mysql)
	* [Cassandra](#cassandra)
<!-- TOC -->

## Overview

The goal of this project is to create a microservices-based backend API to register, list, edit and (soft) delete users.
Please note this codebase is not considered proper in any way and is only intended as a demonstrator.

That said, it does accommodate the following requirements:

1. It is a self-contained Java project (alternatively, see the [Kotlin-based](https://github.com/manosbatsis/spring-cloud-user-management-java/tree/kotlin) branch) based on [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/).
2. The build produces Docker images for all service artifacts and can be run by a single shell script (see sections bellow)
3. When creating a user, the email-service module will send out a welcome email. The project uses
[GreenMail](https://greenmail-mail-test.github.io/greenmail/), an open source email server,
for unit/integration tests and docker (compose) networks.
4. Proper validation with [Jakarta](https://beanvalidation.org/),
horizontally applied [RESTful error handling](https://github.com/wimdeblauwe/error-handling-spring-boot-starter)
and extensive unit/integration tests using [JUnit5](https://junit.org/junit5/) and [TestContainers](https://www.testcontainers.org/).
5. The individual microservices expose REST API documentation using OpenAPI 3, including Swagger UIs, using
[SpringDoc](https://springdoc.org/). Swagger UIs are also aggregated in the gateway module.
6. Elementary microservices, including:
- Gateway service using  [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/reference/html/).
- External Configuration service using [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/).
- Service Registration and Discovery with [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/reference/html/).
- [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)
	using [Spring Cloud Stream](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/)
	and [Kafka](https://kafka.apache.org/).
- Persistence using [Cassandra](https://cassandra.apache.org) and JPA with [MySQL](https://www.mysql.com/).
7. Distributed tracing and timing/latency data with [Zipkin](https://zipkin.io/).
8. A rudimentary UI for user CRUD based on [react-admin](https://marmelab.com/react-admin/).


### Prerequisites

- [Java 17+](https://www.oracle.com/java/technologies/downloads/#java17)
- [Docker](https://www.docker.com/)
- [Docker-Compose](https://docs.docker.com/compose/install/)
- At least 7GB of **free** RAM

### Modules
- gateway-service - Uses Spring Cloud Gateway to act as a proxy/gateway in our architecture.
- config-service: Uses Spring Cloud Config Server as a configuration server in the `native` mode. The configuration files are placed on the classpath.
- discovery-service: Uses Spring Cloud Netflix Eureka as an embedded discovery server.
- user-service: RESTful API for `User` records. Uses JPA for RDBMS persistence.
Acts as a producer for user events.
- event-service: Consumer and RESTful API for user events. Uses Cassandra for persistence.
- email-service: Additional consumer for user events, sends welcome email to new users.
- lib-core and lib-test: Provide trivial utilities to main modules.

## Build HowTo

1. Clone the project from GitHub and navigate to the project directory using a terminal.
2. To build the project and running both unit and integration tests, run:

	`./gradlew build integrationTest`

You can append `-x test` to skip unit tests.
You can also omit `integrationTest` to skip integration tests.

## Test HowTo

### Launch Containers

1. Navigate to the project directory using a terminal and build the docker images:

	`./gradlew build docker -x test`

2. Start the docker compose network and containers ([preview](doc/img/start-apps.png)):

	`./start-apps.sh`


### Manual UI Test

> Before a manual UI test, make sure you have [build and launched](#launch-containers) the containers.

The react-admin web UI is available via the gateway and can be used by pointing your browser to http://localhost:8060

### Manual API Test

> Before a manual API test, make sure you have [build and launched](#launch-containers)  the containers.

To create a user, visit the `user-service` swagger at http://localhost:9080/swagger-ui/index.html
	([preview](https://raw.githubusercontent.com/manosbatsis/spring-cloud-user-management-java/src/doc/img/user-service-swagger-create-user.png)) or run:

```
curl -X 'POST' \
'http://localhost:9080/api/users' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
"email": "name@example.com",
"fullName": "Firstname Lastname",
"address": "Street 12, City, POSTCODE, Country",
"active": true
}'
```

To view the user events, visit the `event-service`  swagger at http://localhost:9081/swagger-ui/index.html
([preview](user-service-swagger-create-user.png)) or run:

```
curl -X 'GET' \
'http://localhost:9081/api/events?userId=1' \
-H 'accept: */*'
}'
```

Stop everything, with:

`./stop-apps.sh`

To remove the Docker images created by this project, go to a terminal and, inside `spring-cloud-user-management` root folder, run the following script

	`./remove-docker-images.sh`

## Reference

### Links

The following URLs are accessible locally when using the start-apps script:

- Gateway Service: http://localhost:8060/swagger-ui.html
- User Service: http://localhost:9080/swagger-ui.html
- Event Service: http://localhost:9081/swagger-ui.html
- Email Service: http://localhost:9082/swagger-ui.html
- Zipkin: http://localhost:9411
- Schema Registry UI: http://localhost:8001
- Kafka Topics UI: http://localhost:8085
- Kafka Manager: http://localhost:9000
- GreenMail: http://localhost:9079

### user-service

	| Environment Variable   | Description                                                                          |
	|------------------------|--------------------------------------------------------------------------------------|
	| `MYSQL_HOST`           | Specify host of the `MySQL` database to use (default `localhost`)                    |
	| `MYSQL_PORT`           | Specify port of the `MySQL` database to use (default `3306`)                         |
	| `KAFKA_HOST`           | Specify host of the `Kafka` message broker to use (default `localhost`)              |
	| `KAFKA_PORT`           | Specify port of the `Kafka` message broker to use (default `29092`)                  |
	| `SCHEMA_REGISTRY_HOST` | Specify host of the `Schema Registry` to use (default `localhost`)                   |
	| `SCHEMA_REGISTRY_PORT` | Specify port of the `Schema Registry` to use (default `8081`)                        |
	| `ZIPKIN_HOST`          | Specify host of the `Zipkin` distributed tracing system to use (default `localhost`) |
	| `ZIPKIN_PORT`          | Specify port of the `Zipkin` distributed tracing system to use (default `9411`)      |

### event-service

	| Environment Variable   | Description                                                                          |
	|------------------------|--------------------------------------------------------------------------------------|
	| `CASSANDRA_HOST`       | Specify host of the `Cassandra` database to use (default `localhost`)                |
	| `CASSANDRA_PORT`       | Specify port of the `Cassandra` database to use (default `9042`)                     |
	| `KAFKA_HOST`           | Specify host of the `Kafka` message broker to use (default `localhost`)              |
	| `KAFKA_PORT`           | Specify port of the `Kafka` message broker to use (default `29092`)                  |
	| `SCHEMA_REGISTRY_HOST` | Specify host of the `Schema Registry` to use (default `localhost`)                   |
	| `SCHEMA_REGISTRY_PORT` | Specify port of the `Schema Registry` to use (default `8081`)                        |
	| `ZIPKIN_HOST`          | Specify host of the `Zipkin` distributed tracing system to use (default `localhost`) |
	| `ZIPKIN_PORT`          | Specify port of the `Zipkin` distributed tracing system to use (default `9411`)      |

### email-service

	| Environment Variable   | Description                                                                          |
	|------------------------|--------------------------------------------------------------------------------------|
	| `EMAIL_HOST`           | Specify the email server host to use (default `127.0.0.1`)                           |
	| `EMAIL_PORT`           | Specify the email server port to use (default `3025`)                                |
	| `EMAIL_USER`           | Specify the email server connection username (default `user`)                        |
	| `EMAIL_PASSWORD        | Specify the email server connection password (default `password`)                    |
	| `KAFKA_HOST`           | Specify host of the `Kafka` message broker to use (default `localhost`)              |
	| `KAFKA_PORT`           | Specify port of the `Kafka` message broker to use (default `29092`)                  |
	| `SCHEMA_REGISTRY_HOST` | Specify host of the `Schema Registry` to use (default `localhost`)                   |
	| `SCHEMA_REGISTRY_PORT` | Specify port of the `Schema Registry` to use (default `8081`)                        |
	| `ZIPKIN_HOST`          | Specify host of the `Zipkin` distributed tracing system to use (default `localhost`) |
	| `ZIPKIN_PORT`          | Specify port of the `Zipkin` distributed tracing system to use (default `9411`)      |


### Useful Commands

#### MySQL

```
docker exec -it -e MYSQL_PWD=secret mysql mysql -uroot --database userdb
SELECT * FROM users;
```
> Type `exit` to leave `MySQL Monitor`

#### Cassandra

```
docker exec -it cassandra cqlsh
USE manosbatsis;
SELECT * FROM user_events;
```
> Type `exit` to leave `CQL shell`
