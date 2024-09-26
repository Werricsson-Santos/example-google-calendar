package dev.werricsson.google_calendar.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Example Google Calendar - RESTful API with Java 21 and Spring Boot 3")
                        .version("v1")
                        .description("Google Calendar API")
                        .termsOfService("")
                        .license(
                                new License()
                                        .name("Example Google Calendar")
                                        .url("")
                        )
                );
    }
}
