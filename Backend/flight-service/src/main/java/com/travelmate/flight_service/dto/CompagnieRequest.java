package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Données pour créer ou modifier une compagnie aérienne")
public record CompagnieRequest(
        @Schema(example = "Tunisair")
        @NotBlank(message = "Le nom de la compagnie est obligatoire")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
        String nomCompagnie,

        @Schema(example = "TU", description = "Code IATA (2 à 3 caractères)")
        @NotBlank(message = "Le code de la compagnie est obligatoire")
        @Pattern(regexp = "^[A-Za-z0-9]{2,3}$", message = "Le code doit contenir 2 à 3 lettres ou chiffres")
        String codeCompagnie,

        @Schema(example = "Tunisie")
        @NotBlank(message = "Le pays est obligatoire")
        @Size(max = 60, message = "Le pays ne doit pas dépasser 60 caractères")
        String paysCompagnie) {
}