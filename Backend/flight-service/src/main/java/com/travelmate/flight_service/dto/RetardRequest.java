package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Nouveaux horaires d'un vol retardé")
public record RetardRequest(

        @Schema(example = "2027-03-10T10:30:00")
        @NotNull(message = "La nouvelle date de départ est obligatoire")
        LocalDateTime nouvelleDateHeureDepart,

        @Schema(example = "2027-03-10T13:00:00")
        @NotNull(message = "La nouvelle date d'arrivée est obligatoire")
        LocalDateTime nouvelleDateHeureArrivee) {
}