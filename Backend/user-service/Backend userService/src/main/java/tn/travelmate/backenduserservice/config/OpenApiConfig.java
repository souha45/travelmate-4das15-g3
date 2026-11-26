package tn.travelmate.backenduserservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /** Ajoute le bouton « Authorize » de Swagger : on y colle le jeton JWT reçu à la connexion. */
    @Bean
    OpenAPI userServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("TravelMate - User Service")
                        .description("Comptes, authentification JWT, profils, préférences de voyage et profils partenaires")
                        .version("0.0.2"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
