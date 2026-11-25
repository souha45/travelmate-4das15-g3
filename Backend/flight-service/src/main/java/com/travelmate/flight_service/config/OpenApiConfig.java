package com.travelmate.flight_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flightServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("TravelMate - Flight Service API")
                .description("API de gestion des vols, compagnies, aéroports et réservations")
                .version("1.0.0"));
    }
}