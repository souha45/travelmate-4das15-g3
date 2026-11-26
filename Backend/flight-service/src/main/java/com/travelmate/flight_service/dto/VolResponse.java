package com.travelmate.flight_service.dto;

import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutVol;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VolResponse(
        Long id,
        String numeroVol,
        LocalDateTime dateHeureDepart,
        LocalDateTime dateHeureArrivee,
        BigDecimal prix,
        int nbrePlacesTotal,
        int nbrePlacesDispo,
        StatutVol statutVol,
        StatutModeration moderation,
        CompagnieResponse compagnie,
        AeroportResponse aeroportDepart,
        AeroportResponse aeroportArrivee) {
}