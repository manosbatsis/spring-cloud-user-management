package com.github.manosbatsis.services.event.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Value("\${spring.application.name}")
    private lateinit var applicationName: String

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI().components(Components()).info(Info().title(applicationName))
    }

    @Bean
    fun customApi(): GroupedOpenApi {
        return GroupedOpenApi.builder().group("api").pathsToMatch("/api/**").build()
    }

    @Bean
    fun actuatorApi(): GroupedOpenApi {
        return GroupedOpenApi.builder().group("actuator").pathsToMatch("/actuator/**").build()
    }
}
