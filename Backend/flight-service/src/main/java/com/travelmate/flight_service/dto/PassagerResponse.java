package com.travelmate.flight_service.dto;

import java.time.LocalDate;

public record PassagerResponse(
        Long id,
        String prenom,
        String nom,
        LocalDate dateNaissance,
        String numPasseport,
        String nationalite,
        String numeroSiege) {
}