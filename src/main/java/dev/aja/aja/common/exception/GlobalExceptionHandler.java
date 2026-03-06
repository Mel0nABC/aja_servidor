package dev.aja.aja.common.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> userNotFound() {
        return ResponseEntity
                .ok(Map.of("success", false, "message", "El usuario al que intentas acceder, no existe"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> incorrectPassword(Exception e) {
        return ResponseEntity
                .ok(Map.of("success", false, "message", e.getMessage()));
    }

}
