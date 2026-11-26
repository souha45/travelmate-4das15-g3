package com.travelmate.flight_service.exception;

// Données incohérentes (dates, départ = arrivée...) -> 400
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) { super(message); }
}