package com.github.manosbatsis.services.user.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.schema.registry.avro.AvroSchemaMessageConverter
import org.springframework.cloud.schema.registry.avro.AvroSchemaServiceManagerImpl
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.messaging.converter.MessageConverter
import org.springframework.util.MimeType

@Configuration
class SchemaRegistryConfig {

    @Value("\${spring.cloud.schema-registry-client.endpoint}")
    private lateinit var endpoint: String

    @Bean
    @ConditionalOnProperty(prefix = "spring.cloud.schema-registry-client", name = ["endpoint"])
    fun schemaRegistryClient(): SchemaRegistryClient {
        val client = ConfluentSchemaRegistryClient()
        client.setEndpoint(endpoint)
        return client
    }

    @Bean
    fun avroSchemaMessageConverter(): MessageConverter {
        val converter = AvroSchemaMessageConverter(
            MimeType.valueOf("application/*+avro"),
            AvroSchemaServiceManagerImpl(),
        )
        converter.setSchemaLocation(ClassPathResource("avro/userevent-message.avsc"))
        return converter
    }
}
