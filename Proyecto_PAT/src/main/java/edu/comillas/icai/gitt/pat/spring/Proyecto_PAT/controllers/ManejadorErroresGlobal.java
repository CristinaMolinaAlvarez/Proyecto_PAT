package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class ManejadorErroresGlobal {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> manejarResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", ex.getStatusCode().value(),
                        "error", ex.getReason()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> manejarValidacion(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Datos inválidos",
                        "detalles", ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(e -> Map.of(
                                        "campo", e.getField(),
                                        "mensaje", e.getDefaultMessage(),
                                        "valor", e.getRejectedValue()
                                ))
                                .toList()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> manejarAccesoDenegado(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 403,
                        "error", "Acceso denegado"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> manejarErrorGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 500,
                        "error", "Error interno del servidor"
                ));
    }
}