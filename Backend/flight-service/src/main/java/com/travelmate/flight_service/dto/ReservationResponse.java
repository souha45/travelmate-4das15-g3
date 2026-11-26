package com.travelmate.flight_service.dto;

import com.travelmate.flight_service.enums.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        String reference,
        Long userId,
        Long paiementId,
        StatutReservation statutReservation,
        BigDecimal montantTotal,
        int nbrePassagers,
        LocalDateTime dateCreation,
        LocalDateTime dateExpiration,
        VolResponse vol,
        List<PassagerResponse> passagers) {
}