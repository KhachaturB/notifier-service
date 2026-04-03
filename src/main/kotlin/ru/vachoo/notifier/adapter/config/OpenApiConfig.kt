package ru.vachoo.notifier.adapter.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

  @Value("\${app.version}") private val appVersion: String = "0.0.0"

  @Bean
  fun customOpenAPI(): OpenAPI {
    return OpenAPI().info(Info().title("Notifier Service API").version(appVersion))
  }
}
