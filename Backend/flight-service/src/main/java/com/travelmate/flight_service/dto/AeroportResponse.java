package com.travelmate.flight_service.dto;

public record AeroportResponse(
        Long id,
        String codeAeroport,
        String nomAeroport,
        String ville,
        String paysAeroport) {
}