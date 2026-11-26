package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Confirmation d'une réservation après paiement")
public record ConfirmationRequest(

        @Schema(example = "501", description = "Id du paiement (référence vers payment-service)")
        @NotNull(message = "L'id du paiement est obligatoire")
        Long paiementId) {
}