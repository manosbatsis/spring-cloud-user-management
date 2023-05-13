#!/usr/bin/env bash

source scripts/my-functions.sh

echo
echo "Starting compose network..."
docker-compose down && docker-compose build --pull && docker-compose up -d
#echo "Please wait..."
#sleep 20s

echo
echo "Starting discovery-service..."

#docker run -a STDERR -a STDOUT -i --rm --name event-service -p 9081:9081 \
docker run -d --rm --name discovery-service -p 8761:8761 \
  -e ZIPKIN_HOST=zipkin \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --network=spring-cloud-user-management-java_default \
  --health-cmd="curl -f http://localhost:8761/eureka/v2/apps || exit 1" \
  manosbatsis/discovery-service:1.0.0
wait_for_container_log "discovery-service" "Started DiscoveryApplication"

echo
echo "Starting event-service..."
# docker run -a STDERR -a STDOUT -i --rm --name event-service -p 9081:9081 \
docker run -d --rm --name event-service -p 9081:9081 \
  -e CASSANDRA_HOST=cassandra -e KAFKA_HOST=kafka -e KAFKA_PORT=9092 \
  -e SCHEMA_REGISTRY_HOST=schema-registry -e ZIPKIN_HOST=zipkin \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --network=spring-cloud-user-management-java_default \
  --health-cmd="curl -f http://localhost:9081/actuator/health || exit 1" \
  manosbatsis/event-service:1.0.0
wait_for_container_log "event-service" "Started EventServiceApplication"

echo
echo "Starting email-service..."
docker run -d --rm --name email-service -p 9082:9082 \
  -e KAFKA_HOST=kafka -e KAFKA_PORT=9092 \
  -e SCHEMA_REGISTRY_HOST=schema-registry -e ZIPKIN_HOST=zipkin \
  -e EMAIL_HOST=greenmail -e EMAIL_PORT=3025 \
  -e EMAIL_USER=user -e EMAIL_PASSWORD=password \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --network=spring-cloud-user-management-java_default \
  --health-cmd="curl -f http://localhost:9092/actuator/health || exit 1" \
  manosbatsis/email-service:1.0.0
wait_for_container_log "email-service" "Started EmailServiceApplication"

echo
echo "Starting user-service..."
docker run -d --rm --name user-service -p 9080:9080 \
  -e MYSQL_HOST=mysql \
  -e KAFKA_HOST=kafka -e KAFKA_PORT=9092 \
  -e SCHEMA_REGISTRY_HOST=schema-registry -e ZIPKIN_HOST=zipkin \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --network=spring-cloud-user-management-java_default \
  --health-cmd="curl -f http://localhost:9080/actuator/health || exit 1" \
  manosbatsis/user-service:1.0.0
wait_for_container_log "user-service" "Started UserServiceApplication"

echo
echo "Done:"
printf "\n"
printf "%14s | %37s |\n" "Application" "URL"
printf "%14s + %37s |\n" "--------------" "-------------------------------------"
printf "%14s | %37s |\n" "user-service" "http://localhost:9080/swagger-ui.html"
printf "%14s | %37s |\n" "event-service" "http://localhost:9081/swagger-ui.html"
printf "%14s | %37s |\n" "greenmail" "http://localhost:9079"
printf "\n"
