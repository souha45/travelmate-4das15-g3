package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Données pour créer ou modifier un vol")
public record VolRequest(

        @Schema(example = "TU750")
        @NotBlank(message = "Le numéro de vol est obligatoire")
        @Pattern(regexp = "^[A-Za-z0-9]{3,8}$", message = "Le numéro de vol doit contenir 3 à 8 lettres ou chiffres")
        String numeroVol,

        @Schema(example = "2027-03-10T08:30:00")
        @NotNull(message = "La date de départ est obligatoire")
        LocalDateTime dateHeureDepart,

        @Schema(example = "2027-03-10T11:00:00")
        @NotNull(message = "La date d'arrivée est obligatoire")
        LocalDateTime dateHeureArrivee,

        @Schema(example = "350.00")
        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
        @Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au plus 2 décimales")
        BigDecimal prix,

        @Schema(example = "180")
        @NotNull(message = "Le nombre de places est obligatoire")
        @Min(value = 1, message = "Il faut au moins 1 place")
        @Max(value = 1000, message = "Maximum 1000 places")
        Integer nbrePlacesTotal,



        @Schema(example = "1") @NotNull(message = "L'aéroport de départ est obligatoire")
        Long aeroportDepartId,

        @Schema(example = "2") @NotNull(message = "L'aéroport d'arrivée est obligatoire")
        Long aeroportArriveeId) {
}