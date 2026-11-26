package com.travelmate.flight_service.dto;

public record CompagnieResponse(
        Long id,
        String nomCompagnie,
        String codeCompagnie,
        String paysCompagnie) {
}