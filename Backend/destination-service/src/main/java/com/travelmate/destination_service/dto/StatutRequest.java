package com.travelmate.destination_service.dto;

import jakarta.validation.constraints.NotNull;

public record StatutRequest(@NotNull Boolean actif) {}
