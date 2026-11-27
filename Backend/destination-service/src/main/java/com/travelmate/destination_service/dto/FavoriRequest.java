package com.travelmate.destination_service.dto;

import jakarta.validation.constraints.NotBlank;

public record FavoriRequest(@NotBlank String destinationId) {}
