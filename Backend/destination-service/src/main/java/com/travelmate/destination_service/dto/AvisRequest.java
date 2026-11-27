package com.travelmate.destination_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AvisRequest(@Min(1) @Max(5) int note, @Size(max = 1000) String commentaire) {}
