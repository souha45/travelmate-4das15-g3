package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.StatutReservation;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long id;
    private Long userId;
    private Long paymentId;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private Integer nbPersonnes;
    private BigDecimal prixTotal;
    private StatutReservation statut;
    private LocalDateTime dateCreation;
    private Long chambreId;
}
