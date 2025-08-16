package simplerag.ragback.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun SimpleRAG(): OpenAPI {
        val info = Info().title("SimpleRAG API").description("SimpleRAG API 명세").version("0.0.1")

        val jwtSchemeName = "JWT TOKEN"
        val securityRequirement = SecurityRequirement().addList(jwtSchemeName)

        val components =
            Components()
                .addSecuritySchemes(
                    jwtSchemeName,
                    SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer")
                        .bearerFormat("JWT")
                )

        return OpenAPI()
            .addServersItem(Server().url("/"))
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}