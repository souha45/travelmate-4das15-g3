package com.travelmate.destination_service.dto;

import com.travelmate.destination_service.enums.TypeDestination;
import com.travelmate.destination_service.model.Coordonnees;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DestinationRequest(
        @NotBlank String nom,
        @NotNull TypeDestination type,
        @NotBlank String pays,
        String codePays,
        String description,
        String climat,
        String meilleureSaison,
        String langue,
        String devise,
        String fuseauHoraire,
        List<String> images,
        @NotNull Coordonnees coordonnees) {}
