package com.travelmate.flight_service.dto;

import com.travelmate.flight_service.enums.StatutCompagnie;

public record CompagnieResponse(
        Long id,
        String nomCompagnie,
        String codeCompagnie,
        String paysCompagnie,
        Long partenaireId,
        StatutCompagnie statut) {
}