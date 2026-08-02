package com.rafael.usuario.api.exceptions;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO padrão para retorno de erros da API
 * Usado pelo GlobalExceptionHandler
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // Usado quando há erros de validação de campos (@Valid)
    private Map<String, String> errors;

    // Construtor para exceções simples (ResourceNotFound, Conflict, etc.)
    public ErrorResponseDTO(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Construtor completo (usado para validação de campos)
    public ErrorResponseDTO(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            Map<String, String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }
}