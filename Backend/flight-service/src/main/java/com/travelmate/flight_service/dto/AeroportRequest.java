package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour créer ou modifier un aéroport")
public record AeroportRequest(

        @Schema(example = "TUN", description = "Code IATA (3 lettres)")
        @NotBlank(message = "Le code de l'aéroport est obligatoire")
        @Pattern(regexp = "^[A-Za-z]{3}$", message = "Le code IATA doit contenir exactement 3 lettres")
        String codeAeroport,

        @Schema(example = "Tunis-Carthage")
        @NotBlank(message = "Le nom de l'aéroport est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String nomAeroport,

        @Schema(example = "Tunis")
        @NotBlank(message = "La ville est obligatoire")
        @Size(max = 60, message = "La ville ne doit pas dépasser 60 caractères")
        String ville,

        @Schema(example = "Tunisie")
        @NotBlank(message = "Le pays est obligatoire")
        @Size(max = 60, message = "Le pays ne doit pas dépasser 60 caractères")
        String paysAeroport) {
}