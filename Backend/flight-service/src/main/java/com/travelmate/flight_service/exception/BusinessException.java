package com.travelmate.flight_service.exception;

// Règle métier violée (ex: suppression interdite, plus de places...) -> 409
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}