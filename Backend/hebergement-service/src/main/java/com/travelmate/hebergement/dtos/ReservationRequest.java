package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.StatutReservation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationRequest {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    private Long paymentId;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDate dateArrivee;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDate dateDepart;

    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    private Integer nbPersonnes;

    @NotNull(message = "Le prix total est obligatoire")
    @Min(value = 0, message = "Le prix total doit être positif")
    private java.math.BigDecimal prixTotal;

    private StatutReservation statut;
}
