package tn.travelmate.backenduserservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorBody> api(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(ErrorBody.of(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" ; "));
        return ResponseEntity.badRequest().body(ErrorBody.of(HttpStatus.BAD_REQUEST, msg));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorBody> illisible(Exception e) {
        return ResponseEntity.badRequest()
                .body(ErrorBody.of(HttpStatus.BAD_REQUEST, "Requête illisible ou paramètre invalide"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorBody> methode(Exception e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorBody.of(HttpStatus.METHOD_NOT_ALLOWED, "Méthode non supportée"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorBody> route(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorBody.of(HttpStatus.NOT_FOUND, "Route introuvable"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorBody> interdit(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorBody.of(HttpStatus.FORBIDDEN, "Accès interdit"));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorBody> nonAuthentifie(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorBody.of(HttpStatus.UNAUTHORIZED, "Jeton absent ou invalide"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorBody> autre(Exception e) {
        log.error("Erreur inattendue", e);
        return ResponseEntity.internalServerError()
                .body(ErrorBody.of(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne"));
    }
}
