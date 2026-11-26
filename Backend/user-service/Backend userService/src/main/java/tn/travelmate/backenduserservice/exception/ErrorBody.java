package tn.travelmate.backenduserservice.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/** Format d'erreur commun à tous les services : status, error, message, timestamp. */
public record ErrorBody(int status, String error, String message, LocalDateTime timestamp) {
    public static ErrorBody of(HttpStatus s, String message) {
        return new ErrorBody(s.value(), s.name(), message, LocalDateTime.now());
    }
}
