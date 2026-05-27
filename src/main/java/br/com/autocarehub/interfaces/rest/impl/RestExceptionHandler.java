package br.com.autocarehub.interfaces.rest.impl;

import br.com.autocarehub.application.ApplicationException;
import br.com.autocarehub.application.ResourceNotFoundException;
import br.com.autocarehub.domain.DomainException;
import br.com.autocarehub.interfaces.rest.generated.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler({ApplicationException.class, DomainException.class})
    public ResponseEntity<ErrorResponse> handleBusinessError(
            RuntimeException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();
        return error(HttpStatus.BAD_REQUEST, "Invalid request body", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return error(
                HttpStatus.BAD_REQUEST, "Malformed or unreadable request body", List.of(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        List<String> details =
                exception.getConstraintViolations().stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .toList();
        return error(HttpStatus.BAD_REQUEST, "Invalid request parameters", details, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "Access denied", List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", List.of(), request);
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status, String message, List<String> details, HttpServletRequest request) {
        ErrorResponse response =
                new ErrorResponse(
                        OffsetDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI())
                        .details(details);
        return ResponseEntity.status(status).body(response);
    }
}
