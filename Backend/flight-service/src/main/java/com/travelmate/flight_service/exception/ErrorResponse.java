package com.travelmate.flight_service.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String erreur,
        String message,
        String chemin,
        List<String> details) {
}