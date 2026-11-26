package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Données pour créer une réservation de vol")
public record ReservationRequest(

        @Schema(example = "1", description = "Id de l'utilisateur (référence vers user-service)")
        @NotNull(message = "L'utilisateur est obligatoire")
        Long userId,

        @Schema(example = "1")
        @NotNull(message = "Le vol est obligatoire")
        Long volId,

        @NotEmpty(message = "Il faut au moins un passager")
        @Size(max = 9, message = "Maximum 9 passagers par réservation")
        @Valid
        List<PassagerRequest> passagers) {
}