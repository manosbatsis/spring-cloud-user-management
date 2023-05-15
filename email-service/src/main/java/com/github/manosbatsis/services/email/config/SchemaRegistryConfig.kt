package com.github.manosbatsis.services.email.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.schema.registry.avro.AvroSchemaMessageConverter
import org.springframework.cloud.schema.registry.avro.AvroSchemaServiceManagerImpl
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient
import org.springframework.cloud.schema.registry.client.SchemaRegistryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MessageConverter
import org.springframework.util.MimeType

@Configuration
class SchemaRegistryConfig {
    @Bean
    fun schemaRegistryClient(
        @Value("\${spring.cloud.schema-registry-client.endpoint}") endpoint: String?,
    ): SchemaRegistryClient {
        val client = ConfluentSchemaRegistryClient()
        client.setEndpoint(endpoint)
        return client
    }

    @Bean
    fun avroSchemaMessageConverter(): MessageConverter {
        return AvroSchemaMessageConverter(
            MimeType.valueOf("application/*+avro"),
            AvroSchemaServiceManagerImpl(),
        )
    }
}
