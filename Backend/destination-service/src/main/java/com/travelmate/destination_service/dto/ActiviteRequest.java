package com.travelmate.destination_service.dto;

import com.travelmate.destination_service.enums.Categorie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record ActiviteRequest(
        @NotBlank String nom,
        String description,
        @NotNull Categorie categorie,
        @PositiveOrZero double prix,
        String devise,
        String lieu,
        String horaires,
        List<String> images) {}
